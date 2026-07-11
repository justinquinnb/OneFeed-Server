package dev.jqb.onefeed.server.feed;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.actor.ActorKey;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.feed.Feed;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.feed.FeedResponse;
import dev.jqb.onefeed.core.platform.Platform;
import dev.jqb.onefeed.server.author.AuthorService;
import dev.jqb.onefeed.server.model.StreamData;
import dev.jqb.onefeed.server.model.StreamedAuthor;
import dev.jqb.onefeed.server.model.StreamedContent;
import dev.jqb.onefeed.server.model.StreamedCursor;
import dev.jqb.onefeed.server.model.StreamedPlatform;
import dev.jqb.onefeed.server.provider.ProviderRegistry;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

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
        Flux<? extends Content> unmappedContentStream;
        if (cursor != null) {
            FeedCursor feedCursor = FeedCursor.fromString(cursor);
            unmappedContentStream = feedService.getRecentContent(feedId, amount, feedCursor);
        } else {
            unmappedContentStream = feedService.getRecentContent(feedId, amount);
        }

        Set<ActorKey> authorKeys = new HashSet<>();
        List<Content> allContent = new ArrayList<>();

        Flux<StreamData> contentStream = unmappedContentStream.doOnNext(content ->
            {
                if (!includeAuthors) {
                    return;
                }

                authorKeys.addAll(content.getAuthorIds().stream().map(authorId ->
                    new ActorKey(providerId, authorId)).toList()
                );

                allContent.add(content);
            }
        ).map(StreamedContent::new);

        // Optionally get the platform data
        Flux<StreamData> platformStream;
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

        return Flux.merge(
            contentStream.concatWith(
                (includeAuthors) ?
                /*
                This is here to make sure that the stream is initialized at a time when the author key
                set is COMPLETE, else we run into the issue where no author keys exist to retrieve.
                 */
                Flux.fromIterable(authorKeys)
                    .flatMap(authorService::getAuthor)
                    .map(StreamedAuthor::new) : Flux.empty()
            ).concatWith(
                // Similar reasoning as above
                Mono.fromCallable(() -> new StreamedCursor(Feed.generateCursor(allContent)))
            ),
            platformStream);
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
