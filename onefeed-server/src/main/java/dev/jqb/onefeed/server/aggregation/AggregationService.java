package dev.jqb.onefeed.server.aggregation;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.actor.OneFeedActor;
import dev.jqb.onefeed.core.aggregation.Aggregation;
import dev.jqb.onefeed.core.aggregation.AggregationOptions;
import dev.jqb.onefeed.core.caching.Cacher;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.content.ContentTransformer;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.feed.Feed;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.provider.Provider;
import dev.jqb.onefeed.server.feed.FeedRegistry;
import dev.jqb.onefeed.server.provider.ProviderRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Responsible for aggregating content from {@link Provider}s
 */
@Service
public class AggregationService {
    private static final Logger logger = LoggerFactory.getLogger(AggregationService.class);

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

    @Autowired
    public AggregationService(FeedRegistry feedRegistry, ProviderRegistry providerRegistry) {
        this.feedRegistry = feedRegistry;
        this.providerRegistry = providerRegistry;
    }

    public Flux<OneFeedContent> aggregate(
        int amount,
        List<FeedId> feedIds,
        AggregationOptions options
    ) {
        return aggregate(amount, feedIds, null, options);
    }

    public Flux<OneFeedContent> aggregate(
        int amount,
        List<FeedId> feedIds,
        FeedCursor aggregateCursor,
        AggregationOptions options
    ) {
        Map<FeedId, Integer> targetAmounts = options.getTargetAmounts(amount);
        List<Feed<? extends Content>> feeds = new ArrayList<>(feedIds.size());
        Map<String, ContentTransformer<? extends Content, OneFeedContent>> normalizers =
            new HashMap<>();
        Map<FeedId, Integer> remainingContentAmounts = new HashMap<>(targetAmounts);

        Map<FeedId, FeedCursor> cursors = new HashMap<>();
        if (aggregateCursor != null) {
            Aggregation.decodeAggregateCursor(aggregateCursor);
        }

        // Check the cache first
        double predictedCacheHitPercent = 0.75;
        int predictedCacheHits = (int) Math.ceil(amount * predictedCacheHitPercent);
        List<OneFeedContent> cachedContent = new ArrayList<>(predictedCacheHits);

        for (FeedId feedId : feedIds) {
            // Retrieve the requested feeds
            Optional<Feed<? extends Content>> possibleFeed = feedRegistry.getFeed(feedId);
            Optional<Provider<? extends Content, ? extends Actor>> possibleProvider =
                providerRegistry.getProvider(feedId);
            if (possibleFeed.isEmpty() || possibleProvider.isEmpty()) {
                continue;
            }
            Feed<? extends Content> feed = possibleFeed.get();
            Provider<? extends Content, ? extends Actor> provider = possibleProvider.get();

            // Keep tabs of the requested feeds and their normalizers
            feeds.add(feed);
            normalizers.put(feedId.getProviderId(), provider.getContentNormalizer());

            // Get the cached content for the feed
            List<OneFeedContent> cachedFeedContent = fetchFromCacheIfAble(feedId,
                targetAmounts.get(feedId), cursors.get(feedId));

            cachedFeedContent.sort(Content::compareTo);
            cachedContent.addAll(cachedFeedContent);
            remainingContentAmounts.put(feedId,
                targetAmounts.get(feedId) - cachedFeedContent.size());
        }

        // Fetch any remaining content from the platforms themselves
        if (cachedContent.size() >= amount) {
            return Flux.fromIterable(cachedContent);
        }

        FeedCursor remainingAggregateCursor = Aggregation.generateAggregateCursor(cachedContent);
        FeedId feedId = new FeedId("onefeed-aggregator", "custom");
        options.setFeedWeights(remainingContentAmounts);
        Aggregation<OneFeedContent> aggregation =
            new Aggregation<>(feedId, feeds, normalizers, options);

        int totalRemainingAmount = amount - cachedContent.size();
        return aggregation.fetchRecentContent(totalRemainingAmount, remainingAggregateCursor)
            .doOnNext(this::cacheIfAble)
            .mergeWith(Flux.fromIterable(cachedContent));
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
