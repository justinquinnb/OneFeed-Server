package dev.jqb.onefeed.server.feed;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.actor.OneFeedActor;
import dev.jqb.onefeed.core.caching.Cacher;
import dev.jqb.onefeed.core.compat.rss.FeedRssChannelAdapter;
import dev.jqb.onefeed.core.compat.rss.Rss2File;
import dev.jqb.onefeed.core.compat.rss.RssAtomLink;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.content.ContentTransformer;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.feed.Feed;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.feed.UnknownFeedIdException;
import dev.jqb.onefeed.core.platform.Platform;
import dev.jqb.onefeed.core.provider.Provider;
import dev.jqb.onefeed.server.config.RssConfig;
import dev.jqb.onefeed.server.provider.ProviderRegistry;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Responsible for providing content from the registered feeds that providers expose
 */
@Service
public class FeedService {
    private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

    /**
     * -- SETTER --
     *  Sets the service that this plugin uses to cache content.
     * @param cache the service that this plugin uses to cache and retrieve content
     */
    @Setter
    @Getter
    private Cacher<OneFeedContent, OneFeedActor> cache;

    private final FeedRegistry feedRegistry;
    private final ProviderRegistry providerRegistry;
    private final RssConfig rssConfig;

    @Autowired
    public FeedService(FeedRegistry feedRegistry, ProviderRegistry providerRegistry,
        RssConfig rssConfig
    ) {
        this.feedRegistry = feedRegistry;
        this.providerRegistry = providerRegistry;
        this.rssConfig = rssConfig;
    }

    /**
     * Fetches the given {@code amount} of most recently published content from {@code this} feed.
     *
     * @param amount the target amount of content to retrieve
     * @return a {@link Flux} that emits a stream of {@link OneFeedContent} containing at most the
     * desired {@code amount} of retrieved content
     */
    public Flux<OneFeedContent> getRecentContent(FeedId feedId, int amount) {
        return getRecentContent(feedId, amount, null);
    }

    /**
     * Fetches the given {@code amount} of most recently published content after the {@code cursor}
     * from {@code this} feed.
     *
     * @param amount the target amount of content to retrieve
     * @param cursor the reference point to start retrieving content from, inclusive
     * @return a {@link Flux} that emits a stream of {@link OneFeedContent} containing at most the
     * desired {@code amount} of retrieved content
     */
    public Flux<OneFeedContent> getRecentContent(FeedId feedId, int amount, FeedCursor cursor) {
        // Retrieve the requested feed
        Optional<Feed<? extends Content>> possibleFeed = feedRegistry.getFeed(feedId);
        Optional<Provider<? extends Content, ? extends Actor>> possibleProvider =
            providerRegistry.getProvider(feedId);

        if (possibleFeed.isEmpty() || possibleProvider.isEmpty()) {
            throw new UnknownFeedIdException(feedId);
        }

        Feed<? extends Content> feed = possibleFeed.get();
        Provider<? extends Content, ? extends Actor> provider = possibleProvider.get();
        ContentTransformer<Content, OneFeedContent> normalizer =
            (ContentTransformer<Content, OneFeedContent>) provider.getContentNormalizer();

        // Get the cached content for the feed
        List<OneFeedContent> cachedContent = fetchFromCacheIfAble(feedId, amount, cursor);

        // Fetch any remaining content from the platforms themselves
        if (cachedContent.size() >= amount) {
            return Flux.fromIterable(cachedContent);
        }

        int remainingContentNeeded = amount - cachedContent.size();

        // Cache is empty, so fetch everything from the platform itself
        if (remainingContentNeeded == amount) {
            Flux<? extends Content> baseContentStream;
            if (cursor != null) {
                baseContentStream = feed.fetchRecentContent(remainingContentNeeded, cursor);
            } else {
                baseContentStream = feed.fetchRecentContent(remainingContentNeeded);
            }

            return baseContentStream
                .map(normalizer::transform)
                .doOnNext(this::cacheIfAble)
                .mergeWith(Flux.fromIterable(cachedContent));
        }

        FeedCursor newCursor = Feed.generateCursor(cachedContent);

        return feed.fetchRecentContent(remainingContentNeeded, newCursor)
            .map(normalizer::transform)
            .doOnNext(this::cacheIfAble)
            .mergeWith(Flux.fromIterable(cachedContent));
    }

    /**
     * Gets an RSS feed of the given {@code feedId}'s most recent content.
     * @param feedId the ID of the feed to build an RSS feed from
     * @param servedAt the URL the feed will be served on
     *
     * @return an RSS feed of the given {@code feedId}'s most recent content, at most
     * {@link RssConfig#getMaxChannelItems()}
     */
    public Rss2File getAsRssFeed(FeedId feedId, String servedAt) {
        Optional<Feed<? extends Content>> possibleFeed = feedRegistry.getFeed(feedId);
        Optional<Provider<? extends Content, ? extends Actor>> possibleProvider =
            providerRegistry.getProvider(feedId);

        if (possibleFeed.isEmpty() || possibleProvider.isEmpty()) {
            throw new UnknownFeedIdException(feedId);
        }

        Feed<Content> feed = (Feed<Content>) possibleFeed.get();
        Provider<Content, Actor> provider = (Provider<Content, Actor>) possibleProvider.get();
        ContentTransformer<Content, OneFeedContent> normalizer = provider.getContentNormalizer();
        Platform platform = provider.getPlatform();

        String channelTitle = String.format("%s Feed '%s'", platform.getName(), feed.getId().feedName());
        String channelDescription = String.format("An RSS feed of %s content generated by a OneFeed user.", platform.getName());

        FeedRssChannelAdapter<? extends Content> rssChannel = new FeedRssChannelAdapter<>(
            feed, normalizer, channelTitle, channelDescription, servedAt, rssConfig.getMaxChannelItems());

        return new Rss2File(rssChannel);
    }

    /**
     * Fetches the given amount of content from the cache if the cache is set.
     * @param feedId the ID of the feed to retrieve content from
     * @param amount the amount of content to retrieve
     * @param cursor the cursor to retrieve content after, inclusive
     * @return the desired content from the cache
     */
    private List<OneFeedContent> fetchFromCacheIfAble(FeedId feedId, int amount, FeedCursor cursor) {
        if (cache == null) {
            return List.of();
        }

        try {
            if (cursor == null) {
                return cache.fetchRecentContent(feedId, amount);
            }

            return cache.fetchRecentContent(feedId, amount, cursor);
        } catch (Exception e) {
            logger.error("Error fetching content from cache", e);
            return List.of();
        }
    }

    /**
     * Caches the given content if the cache is set.
     * @param content the piece of {@link OneFeedContent} to cache if the cache is set
     */
    private void cacheIfAble(OneFeedContent content) {
        if (cache != null) {
            cache.cacheContent(List.of(content));
        }
    }
}
