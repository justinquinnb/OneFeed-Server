package dev.jqb.onefeed.server.feed;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.actor.ActorKey;
import dev.jqb.onefeed.core.aggregation.Aggregation;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.feed.Feed;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.feed.FeedResponse;
import dev.jqb.onefeed.core.platform.Platform;
import dev.jqb.onefeed.server.aggregation.AggregationService;
import dev.jqb.onefeed.server.author.AuthorService;
import dev.jqb.onefeed.server.model.CustomAggregation;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Endpoints to get content from a single feeds
 */
@RestController
@Validated
@RequestMapping("/feed")
@Tag(name = "Feed", description = "Endpoints for getting content from a single feed")
public class FeedController {
    private final FeedService feedService;
    private final AuthorService authorService;
    private final ProviderRegistry providerRegistry;

    @Autowired
    public FeedController(FeedService feedService, AuthorService authorService,
        ProviderRegistry providerRegistry
    ) {
        this.feedService = feedService;
        this.authorService = authorService;
        this.providerRegistry = providerRegistry;
    }

    /**
     * Gets a stream of content and (optionally) authors and platform data from the desired feed.
     *
     * @param providerId the ID of the provider whose feed's content to retrieve
     * @param feedName the name of the feed whose content to retrieve
     * @param amount the amount of content to try getting
     * @param includeAuthors whether to include the authors of the content (optional, defaults to
     * {@code true})
     * @param includePlatforms whether to include the data about the feed's platform (optional,
     *                         defaults to {@code false})
     * @param cursor the point to start retrieving content after, inclusively (optional)
     *
     * @return a stream of content and authors representing the desired data from the given feed
     */
    @GetMapping("/stream/{providerId}/{feedName}")
    public Flux<StreamData> getFeedStream(
        @PathVariable String providerId,
        @PathVariable String feedName,
        @RequestParam @Min(1) int amount,
        @RequestParam(defaultValue = "true") Boolean includeAuthors,
        @RequestParam(defaultValue = "false") Boolean includePlatforms,
        @RequestParam(required = false) String cursor
    ) {
        // Get the content stream
        FeedId feedId = new FeedId(providerId, feedName);
        Flux<OneFeedContent> contentStream;
        if (cursor != null) {
            FeedCursor feedCursor = FeedCursor.fromString(cursor);
            contentStream = feedService.getRecentContent(feedId, amount, feedCursor);
        } else {
            contentStream = feedService.getRecentContent(feedId, amount);
        }

        // Collect all the content for processing
        List<OneFeedContent> allContent = new ArrayList<>();
        Set<ActorKey> authorKeys = new HashSet<>();

        Flux<StreamedContent> contentUpdateStream = contentStream
            .doOnNext((ofc) -> {
                allContent.add(ofc);
                authorKeys.addAll(ofc.getAuthorIds().stream().map((
                    id) -> new ActorKey(ofc.getFeedId().getProviderId(), id)).toList());
            })
            .map(StreamedContent::new);

        // Optionally get the author stream
        // TODO (same as Aggregation controller todo)
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
            if (providerRegistry.getProvider(feedId).isPresent()) {
                Platform platform = providerRegistry.getProvider(feedId).get().getPlatform();
                platforms.add(new StreamedPlatform(platform));
            }
            platformStream = Flux.fromIterable(platforms);
        } else {
            platformStream = Flux.empty();
        }

        return Flux.merge(contentUpdateStream, authorStream, platformStream)
            .concatWith(
                Mono.fromCallable(
                    () -> new StreamedCursor(Feed.generateCursor(allContent))
                )
            );
    }

    /**
     * Gets a complete batch of content and (optionally) authors and platform data from the desired
     * feed.
     *
     * @param providerId the ID of the provider whose feed's content to retrieve
     * @param feedName the name of the feed whose content to retrieve
     * @param amount the amount of content to try getting
     * @param includeAuthors whether to include the authors of the content (optional, defaults to
     * {@code true})
     * @param includePlatforms whether to include the data about the feed's platform (optional,
     *                         defaults to {@code false})
     * @param cursor the point to start retrieving content after, inclusively (optional)
     *
     * @return a complete batch of content and authors representing the desired data from the given
     * feed
     */
    @GetMapping("batch/{providerId}/{feedName}")
    public FeedResponse getFeedBatch(
        @PathVariable String providerId,
        @PathVariable String feedName,
        @RequestParam @Min(1) int amount,
        @RequestParam(defaultValue = "true") Boolean includeAuthors,
        @RequestParam(defaultValue = "false") Boolean includePlatforms,
        @RequestParam(required = false) String cursor
    ) {
        Flux<StreamData> stream = getFeedStream(providerId, feedName, amount, includeAuthors,
            includePlatforms, cursor);

        List<StreamData> streamData = stream.collectList().block();

        List<Content> content = new ArrayList<>();
        Platform platform = null;
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
                    platform = pu.getPlatform();
                    break;
                default:
                    break;
            }
        }

        return new FeedResponse(authors, platform, content, nextCursor);
    }
}
