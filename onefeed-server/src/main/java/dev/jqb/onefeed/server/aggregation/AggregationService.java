package dev.jqb.onefeed.server.aggregation;

import dev.jqb.onefeed.core.aggregation.AggregationOptions;
import dev.jqb.onefeed.core.aggregation.Aggregator;
import dev.jqb.onefeed.core.caching.Cacher;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.Feed;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.provider.Provider;
import dev.jqb.onefeed.core.content.OneFeedContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Responsible for aggregating content from {@link Provider}s
 */
@Service
public class AggregationService implements Aggregator<OneFeedContent> {
    private static final Logger logger = LoggerFactory.getLogger(AggregationService.class);

    /**
     * -- SETTER --
     *  Sets the service that this plugin uses to cache content.
     * @param cache the service that this plugin uses to cache and retrieve content
     */
    @Setter
    @Getter
    private Cacher cache;

    @Override
    public Flux<OneFeedContent> aggregate(
        int amount,
        List<Feed<? extends PlatformContent, ? extends PlatformActor>> feeds,
        AggregationOptions options
    ) {
        Map<FeedId, Integer> targetAmounts = options.getTargetAmounts(amount);
        List<Flux<OneFeedContent>> normalizedContentStreams = new ArrayList<>(feeds.size());

        for (Feed<? extends PlatformContent, ? extends PlatformActor> feed : feeds) {
            Provider<? extends PlatformContent, ? extends PlatformActor> provider = feed.getProvider();
            ContentNormalizer<PlatformContent, OneFeedContent> contentNormalizer =
                (ContentNormalizer<PlatformContent, OneFeedContent>) provider.getContentNormalizer();
            String feedName = feed.getId().getFeedName();

            // TODO check cache first
            Flux<? extends PlatformContent> feedStream = provider.fetchRecentContent(feedName,
                targetAmounts.get(feed.getId()));

            normalizedContentStreams.add(
                feedStream
                    .map(contentNormalizer::normalize)
                    .doOnError(err -> logger.warn(
                        "Error fetching content from feed '{}': {}", feedName, err.getStackTrace()))
                    .onErrorComplete()
            );
        }

        return Flux.merge(normalizedContentStreams)
            .doOnNext(this::cacheIfAble);
    }

    @Override
    public Flux<OneFeedContent> aggregate(
        int amount,
        List<Feed<? extends PlatformContent, ? extends PlatformActor>> feeds,
        Map<FeedId, ? extends FeedCursor> cursors,
        AggregationOptions options
    ) {
        Map<FeedId, Integer> targetAmounts = options.getTargetAmounts(amount);
        List<Flux<OneFeedContent>> normalizedContentStreams = new ArrayList<>(feeds.size());

        for (Feed<? extends PlatformContent, ? extends PlatformActor> feed : feeds) {
            Provider<? extends PlatformContent, ? extends PlatformActor> provider = feed.getProvider();
            ContentNormalizer<PlatformContent, OneFeedContent> contentNormalizer =
                (ContentNormalizer<PlatformContent, OneFeedContent>) provider.getContentNormalizer();
            String feedName = feed.getId().getFeedName();

            // TODO check cache first

            Flux<? extends PlatformContent> feedStream = provider.fetchRecentContent(feedName,
                targetAmounts.get(feed.getId()), cursors.get(feed.getId()));

            normalizedContentStreams.add(
                feedStream
                    .map(contentNormalizer::normalize)
                    .doOnError(err -> logger.warn(
                        "Error fetching content from feed '{}': {}", feedName, err.getStackTrace()))
                    .onErrorComplete()
            );
        }

        return Flux.merge(normalizedContentStreams)
            .doOnNext(this::cacheIfAble);
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
