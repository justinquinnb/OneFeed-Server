package dev.jqb.onefeed.server.aggregation;

import com.fasterxml.jackson.annotation.JsonView;
import dev.jqb.onefeed.core.actor.ActorKey;
import dev.jqb.onefeed.core.aggregation.Aggregation;
import dev.jqb.onefeed.core.aggregation.AggregationOptions;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.platform.Platform;
import dev.jqb.onefeed.server.aggregation.CustomAggregation.WeightedFeed;
import dev.jqb.onefeed.server.author.AuthorService;
import dev.jqb.onefeed.server.provider.ProviderRegistry;
import dev.jqb.onefeed.server.response.StreamElement;
import dev.jqb.onefeed.server.response.std.AggregationResponse;
import dev.jqb.onefeed.server.response.std.FeedCursorResponse;
import dev.jqb.onefeed.server.response.std.OneFeedActorResponse;
import dev.jqb.onefeed.server.response.std.OneFeedContentResponse;
import dev.jqb.onefeed.server.response.std.PlatformResponse;
import dev.jqb.onefeed.server.response.std.StdResponseMapper;
import dev.jqb.onefeed.server.response.Streamable;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Endpoints to get aggregations of content from multiple feeds at once
 */
@RestController
@Validated
@RequestMapping("/aggregation")
@Tag(name = "Aggregation", description = "Endpoints for aggregating content from multiple feeds")
public class AggregationController {
    private final AggregationService aggregationService;
    private final AuthorService authorService;
    private final ProviderRegistry providerRegistry;
    private final StdResponseMapper responseMapper;

    @Autowired
    public AggregationController(AggregationService aggregationService, AuthorService authorService,
        ProviderRegistry providerRegistry, StdResponseMapper responseMapper
    ) {
        this.aggregationService = aggregationService;
        this.authorService = authorService;
        this.providerRegistry = providerRegistry;
        this.responseMapper = responseMapper;
    }

    /**
     * Gets a stream of content and (optionally) its author and platform data from the given feeds.
     *
     * @param amount the total amount of content to try getting
     * @param customAggregation the combination of feed IDs and optional weights to use in the
     *                          aggregation
     * @param includeAuthors whether to include the authors of the aggregated content
     *                       (optional, defaults to {@code true})
     * @param includePlatforms whether to include the data about each feed's platform (optional,
     *                         defaults to {@code false})
     * @param aggregateCursor the point to start retrieving content after, inclusively (optional)
     *
     * @return a stream of content and authors representing the desired data from the given feeds,
     * emitted as soon as it's available. If a {@link FeedId} in the {@code customAggregation}
     * is invalid, data from it will not be included in the aggregation.
     */
    @PostMapping("/custom/stream")
    public Flux<StreamElement<? extends Streamable>> getCustomAggregationStream(
        @RequestParam @Min(1) int amount,
        @RequestBody @Valid CustomAggregation customAggregation,
        @RequestParam(defaultValue = "true") Boolean includeAuthors,
        @RequestParam(defaultValue = "false") Boolean includePlatforms,
        @RequestParam(required = false) String aggregateCursor
    ) {
        // Get the feed IDs first
        List<FeedId> feedIds = customAggregation.getWeightedFeeds().stream().map(
            WeightedFeed::getFeedId).toList();

        // Convert the weights to the map required by the aggregator
        HashMap<FeedId, Integer> weights = new HashMap<>();
        for (WeightedFeed fw : customAggregation.getWeightedFeeds()) {
            if (fw.getWeight() == null) {
                weights.put(fw.getFeedId(), 1);
            } else {
                weights.put(fw.getFeedId(), fw.getWeight());
            }
        }

        // Get the content stream
        AggregationOptions aggOptions = new AggregationOptions(weights);
        Flux<OneFeedContent> contentStream;

        if (aggregateCursor != null && !aggregateCursor.isBlank()) {
            FeedCursor cursor = FeedCursor.fromString(aggregateCursor);
            contentStream = aggregationService.aggregate(amount, feedIds, cursor, aggOptions);
        } else {
            contentStream = aggregationService.aggregate(amount, feedIds, aggOptions);
        }

        // Collect all the content for aggregate cursor generation
        Set<ActorKey> authorKeys = new HashSet<>();
        List<Content> allContent = new ArrayList<>();

        Flux<StreamElement<?>> contentResponseStream = contentStream.doOnNext(content ->
            {
                if (!includeAuthors) {
                    return;
                }

                authorKeys.addAll(content.getAuthorIds().stream().map(authorId ->
                    new ActorKey(content.getProviderId(), authorId)).toList()
                );

                allContent.add(content);
            }
        ).map(responseMapper::toOneFeedContentResponse
        ).map(StreamElement::new);

        // Optionally get the platform data
        Flux<StreamElement<?>> platformResponseStream;
        if (includePlatforms) {
            Set<String> providerIds = new HashSet<>();
            for (FeedId feedId : feedIds) {
                providerIds.add(feedId.getProviderId());
            }

            List<StreamElement<?>> platforms = new ArrayList<>();
            for (String providerId : providerIds) {
                if (providerRegistry.getProvider(providerId).isPresent()) {
                    Platform platform = providerRegistry.getProvider(providerId).get().getPlatform();
                    platforms.add(new StreamElement<>(responseMapper.toPlatformResponse(platform)));
                }
            }

            platformResponseStream = Flux.fromIterable(platforms);
        } else {
            platformResponseStream = Flux.empty();
        }

        return Flux.merge(
            contentResponseStream.concatWith(
                (includeAuthors) ?
                /*
                This is here to make sure that the stream is initialized at a time when the author
                key set is COMPLETE, else we run into the issue where no author keys exist to
                retrieve.
                 */
                    Flux.fromIterable(authorKeys)
                        .flatMap(authorService::getAuthor)
                        .map(responseMapper::toOneFeedActorResponse)
                        .map(StreamElement::new) : Flux.empty()
            ).concatWith(
                // Similar reasoning as above
                Mono.fromCallable(() -> new StreamElement<>(responseMapper.toCursorResponse(
                    Aggregation.generateAggregateCursor(allContent))
                ))
            ),
            platformResponseStream);
    }

    /**
     * Gets a complete aggregation of content and (optionally) its author and platform data from the
     * given feeds.
     *
     * @param amount the total amount of content to retrieve
     * @param customAggregation the combination of feed IDs and optional weights to use in the
     *                          aggregation
     * @param includeAuthors whether to include the authors of the aggregated content
     *                       (optional, defaults to {@code true})
     * @param includePlatforms whether to include the data about each feed's platform (optional,
     *                         defaults to {@code false})
     * @param aggregateCursor the point to start retrieving content after, inclusively (optional)
     *
     * @return complete, structured aggregation data of the desired amount of content, author, and
     * platform data from the given feeds. If a {@link FeedId} in the {@code customAggregation}
     * is invalid, data from it will not be included in the aggregation.
     */
    @PostMapping("/custom/batch")
    public AggregationResponse getCustomAggregationBatch(
        @RequestParam @Min(1) int amount,
        @RequestBody @Valid CustomAggregation customAggregation,
        @RequestParam(defaultValue = "true") Boolean includeAuthors,
        @RequestParam(defaultValue = "false") Boolean includePlatforms,
        @RequestParam(required = false) String aggregateCursor
    ) {
        Flux<StreamElement<?>> stream = getCustomAggregationStream(
            amount, customAggregation, includeAuthors, includePlatforms, aggregateCursor);

        List<StreamElement<?>> streamElements = stream.collectList().block();

        List<OneFeedContentResponse> content = new ArrayList<>();
        Map<String, PlatformResponse> platforms = new HashMap<>();
        Map<ActorKey, OneFeedActorResponse> authors = new HashMap<>();

        FeedCursorResponse nextCursor = null;

        // Organize the data
        for (StreamElement<?> streamElement : streamElements) {
            switch (streamElement.getData()) {
                case OneFeedContentResponse c:
                    content.add(c);
                    break;
                case OneFeedActorResponse a:
                    authors.put(new ActorKey(a.providerId(), a.externalRef().id()), a);
                    break;
                case FeedCursorResponse f:
                    nextCursor = f;
                    break;
                case PlatformResponse p:
                    platforms.put(p.providerId(), p);
                    break;
                default:
                    break;
            }
        }

        // Build the aggregation object
        return new AggregationResponse(authors, platforms, content, nextCursor);
    }

    // TODO implement alongside presets. Can't do it until then because RSS feeds must be get
    // requests, which the custom variant just can't support (yet?)
//    /**
//     * Gets the desired, preconfigured aggregation as an RSS feed.
//     *
//     * @param id the ID of the preconfigured aggregation to retrieve
//     *
//     * @return an RSS-compatible aggregation of the desired amount of content from the given feeds
//     */
//    @PostMapping(path="/rss/preset/{id}", produces=MediaType.APPLICATION_XML_VALUE)
//    public Rss2File getCustomAggregationRss(@PathVariable String id) {
//
//    }

//    /**
//     * Gets a preconfigured aggregation of the desired amount of content after the given cursor.
//     *
//     * @param id the ID of the preconfigured aggregation to retrieve
//     * @param aggregateCursor the point to start retrieving content after, inclusively (optional)
//     * @return the preconfigured aggregation
//     */
//    @GetMapping("/stream/preset/{id}")
//    public Flux<Streamable> getPreconfiguredAggregationStream(
//        @PathVariable @NotBlank String id,
//        @RequestParam @Min(1) int amount,
//        @RequestParam(required = false) String aggregateCursor
//    ) {
//        // TODO implement
//        return Flux.empty();
//    }
}
