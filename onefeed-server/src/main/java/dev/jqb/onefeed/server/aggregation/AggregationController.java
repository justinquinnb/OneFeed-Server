package dev.jqb.onefeed.server.aggregation;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.actor.ActorKey;
import dev.jqb.onefeed.core.aggregation.Aggregation;
import dev.jqb.onefeed.core.aggregation.AggregationOptions;
import dev.jqb.onefeed.core.aggregation.AggregationResponse;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.platform.Platform;
import dev.jqb.onefeed.server.author.AuthorService;
import dev.jqb.onefeed.server.model.CustomAggregation;
import dev.jqb.onefeed.server.model.CustomAggregation.WeightedFeed;
import dev.jqb.onefeed.server.model.StreamData;
import dev.jqb.onefeed.server.model.StreamedAuthor;
import dev.jqb.onefeed.server.model.StreamedContent;
import dev.jqb.onefeed.server.model.StreamedCursor;
import dev.jqb.onefeed.server.model.StreamedPlatform;
import dev.jqb.onefeed.server.provider.ProviderRegistry;
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

    @Autowired
    public AggregationController(AggregationService aggregationService, AuthorService authorService,
        ProviderRegistry providerRegistry
    ) {
        this.aggregationService = aggregationService;
        this.authorService = authorService;
        this.providerRegistry = providerRegistry;
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
    public Flux<StreamData> getCustomAggregationStream(
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
        List<OneFeedContent> allContent = new ArrayList<>();
        Set<ActorKey> authorKeys = new HashSet<>();

        // TODO!! probably need to change this a bit bc authorKeys won't have anything by the
        // time the below authorService call needs it
        Flux<StreamedContent> contentUpdateStream = contentStream
            .doOnNext((ofc) -> {
                allContent.add(ofc);
                authorKeys.addAll(ofc.getAuthorIds().stream().map((
                    id) -> new ActorKey(ofc.getFeedId().getProviderId(), id)).toList());
            })
            .map(StreamedContent::new);

        // Optionally get the author stream
        Flux<StreamedAuthor> authorStream;
        if (includeAuthors) {
            authorStream = authorService.getAuthors(authorKeys).map(StreamedAuthor::new);
        } else {
            authorStream = Flux.empty();
        }

        // Optionally get the platform data
        Flux<StreamedPlatform> platformStream;
        if (includePlatforms) {
            List<StreamedPlatform> platforms = new ArrayList<>();
            for (FeedId feedId : feedIds) {
                if (providerRegistry.getProvider(feedId).isPresent()) {
                    Platform platform = providerRegistry.getProvider(feedId).get().getPlatform();
                    platforms.add(new StreamedPlatform(platform));
                }
            }
            platformStream = Flux.fromIterable(platforms);
        } else {
            platformStream = Flux.empty();
        }

        return Flux.merge(contentUpdateStream, authorStream, platformStream)
            .concatWith(
                Mono.fromCallable(
                    () -> new StreamedCursor(Aggregation.generateAggregateCursor(allContent))
                )
            );
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
        Flux<StreamData> stream = getCustomAggregationStream(
            amount, customAggregation, includeAuthors, includePlatforms, aggregateCursor);

        List<StreamData> streamData = stream.collectList().block();

        List<Content> content = new ArrayList<>();
        Map<String, Platform> platforms = new HashMap<>();
        Map<ActorKey, Actor> authors = new HashMap<>();

        FeedCursor nextCursor = null;

        // Organize the data
        for (StreamData streamDataObj : streamData) {
            switch (streamDataObj) {
                case StreamedContent cu:
                    content.add(cu.getContent());
                    break;
                case StreamedAuthor au:
                    authors.put(au.getAuthor().getKey(), au.getAuthor());
                    break;
                case StreamedCursor cu:
                    nextCursor = cu.getAggregateCursor();
                    break;
                case StreamedPlatform pu:
                    platforms.put(pu.getPlatform().getProviderId(), pu.getPlatform());
                    break;
                default:
                    break;
            }
        }

        // Build the aggregation object
        return new AggregationResponse(authors, platforms, content, nextCursor);
    }

    /**
     * Gets an RSS-compatible aggregation of the desired amount of content from the given feeds.
     *
     * @param amount the total amount of content to retrieve
     * @param customAggregation the combination of feed IDs and optional weights to use in the
     *                          aggregation
     * @param includeAuthors whether to include the authors of the aggregated content
     *                       (optional, defaults to {@code true})
     * @param aggregateCursor the point to start retrieving content after, inclusively (optional)
     *
     * @return an RSS-compatible aggregation of the desired amount of content from the given feeds
     */
//    @PostMapping("/rss/custom")
//    public RssFeed getCustomAggregationRss(
//        @RequestParam @Min(1) int amount,
//        @RequestBody @Valid CustomAggregation customAggregation,
//        @RequestParam(defaultValue = "true") Boolean includeAuthors,
//        @RequestParam(required = false) String aggregateCursor
//    ) {
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
//    public Flux<StreamData> getPreconfiguredAggregationStream(
//        @PathVariable @NotBlank String id,
//        @RequestParam @Min(1) int amount,
//        @RequestParam(required = false) String aggregateCursor
//    ) {
//        // TODO implement
//        return Flux.empty();
//    }
}
