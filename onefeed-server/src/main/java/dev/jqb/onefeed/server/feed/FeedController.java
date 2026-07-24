package dev.jqb.onefeed.server.feed;

import dev.jqb.onefeed.core.actor.ActorKey;
import dev.jqb.onefeed.core.compat.rss.Rss2File;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.feed.Feed;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.platform.Platform;
import dev.jqb.onefeed.server.author.AuthorService;
import dev.jqb.onefeed.server.provider.ProviderRegistry;
import dev.jqb.onefeed.server.response.std.FeedCursorResponse;
import dev.jqb.onefeed.server.response.std.FeedResponse;
import dev.jqb.onefeed.server.response.std.OneFeedActorResponse;
import dev.jqb.onefeed.server.response.std.OneFeedContentResponse;
import dev.jqb.onefeed.server.response.std.PlatformResponse;
import dev.jqb.onefeed.server.response.std.StdResponseMapper;
import dev.jqb.onefeed.server.response.std.Streamable;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    private final FeedService feedService;
    private final AuthorService authorService;
    private final ProviderRegistry providerRegistry;
    private final StdResponseMapper responseMapper;

    @Autowired
    public FeedController(FeedService feedService, AuthorService authorService,
        ProviderRegistry providerRegistry, StdResponseMapper responseMapper
    ) {
        this.feedService = feedService;
        this.authorService = authorService;
        this.providerRegistry = providerRegistry;
        this.responseMapper = responseMapper;
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
    @GetMapping("{providerId}/{feedName}/stream")
    public Flux<Streamable> getFeedStream(
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

        Set<ActorKey> authorKeys = new HashSet<>();
        List<Content> allContent = new ArrayList<>();

        Flux<Streamable> contentResponseStream = contentStream.doOnNext(content ->
            {
                if (!includeAuthors) {
                    return;
                }

                authorKeys.addAll(content.getAuthorIds().stream().map(authorId ->
                    new ActorKey(providerId, authorId)).toList()
                );

                allContent.add(content);
            }
        ).map(responseMapper::toOneFeedContentResponse);

        // Optionally get the platform data
        Flux<Streamable> platformResponseStream;
        if (includePlatforms) {
            List<PlatformResponse> platforms = new ArrayList<>();
            if (providerRegistry.getProvider(feedId).isPresent()) {
                Platform platform = providerRegistry.getProvider(feedId).get().getPlatform();
                platforms.add(responseMapper.toPlatformResponse(platform));
            }
            platformResponseStream = Flux.fromIterable(platforms);
        } else {
            platformResponseStream = Flux.empty();
        }

        return Flux.merge(
            contentResponseStream.concatWith(
                (includeAuthors) ?
                /*
                This is here to make sure that the stream is initialized at a time when the author key
                set is COMPLETE, else we run into the issue where no author keys exist to retrieve.
                 */
                Flux.fromIterable(authorKeys)
                    .flatMap(authorService::getAuthor)
                    .map(responseMapper::toOneFeedActorResponse) : Flux.empty()
            ).concatWith(
                // Similar reasoning as above
                Mono.fromCallable(() -> responseMapper.toCursorResponse(Feed.generateCursor(allContent)))
            ),
            platformResponseStream
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
    @GetMapping("{providerId}/{feedName}/batch")
    public FeedResponse getFeedBatch(
        @PathVariable String providerId,
        @PathVariable String feedName,
        @RequestParam @Min(1) int amount,
        @RequestParam(defaultValue = "true") Boolean includeAuthors,
        @RequestParam(defaultValue = "false") Boolean includePlatforms,
        @RequestParam(required = false) String cursor
    ) {
        Flux<Streamable> stream = getFeedStream(providerId, feedName, amount, includeAuthors,
            includePlatforms, cursor);

        List<Streamable> streamData = stream.collectList().block();

        List<OneFeedContentResponse> content = new ArrayList<>();
        PlatformResponse platform = null;
        Map<ActorKey, OneFeedActorResponse> authors = new HashMap<>();

        FeedCursorResponse nextCursor = null;

        // Organize the data
        for (Streamable streamDataObj : streamData) {
            switch (streamDataObj) {
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
                    platform = p;
                    break;
                default:
                    break;
            }
        }

        return new FeedResponse(authors, platform, content, nextCursor);
    }

    /**
     * Gets a feed's content formatted as a valid RSS XML document.
     *
     * @param providerId the ID of the provider whose feed's content to retrieve
     * @param feedName the name of the feed whose content to retrieve
     * @param request the HTTP request, used to get the root URL
     *
     * @return a complete batch of content and authors representing the desired data from the given
     * feed
     */
    @GetMapping(path="{providerId}/{feedName}/rss", produces=MediaType.APPLICATION_RSS_XML_VALUE)
    public Rss2File getFeedRss(
        @PathVariable String providerId,
        @PathVariable String feedName,
        HttpServletRequest request
    ) {
        FeedId feedId = new FeedId(providerId, feedName);
        return feedService.getAsRssFeed(feedId, request.getRequestURL().toString());
    }
}
