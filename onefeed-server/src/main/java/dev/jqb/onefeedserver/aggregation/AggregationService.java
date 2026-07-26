package dev.jqb.onefeedserver.aggregation;

import dev.jqb.onefeed.core.actor.OneFeedActor;
import dev.jqb.onefeed.core.aggregation.Aggregation;
import dev.jqb.onefeed.core.aggregation.AggregationOptions;
import dev.jqb.onefeed.core.caching.Cacher;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.provider.Provider;
import dev.jqb.onefeedserver.feed.FeedService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private final FeedService feedService;

    @Autowired
    public AggregationService(FeedService feedService) {
        this.feedService = feedService;
    }

    /**
     * Aggregates the {@code amount} most recent content from the feeds with the given
     * {@code feedIds}, the final aggregation adhering to the options specified by {@code options}.
     *
     * @param amount the amount of content to retrieve
     * @param feedIds the IDs of the feeds to aggregate content from
     * @param options the options dictating the final aggregation
     *
     * @return the aggregated content
     */
    public Flux<OneFeedContent> aggregate(
        int amount,
        List<FeedId> feedIds,
        AggregationOptions options
    ) {
        return aggregate(amount, feedIds, null, options);
    }

    /**
     * Aggregates the {@code amount} most recent content from the feeds with the given
     * {@code feedIds}, the final aggregation adhering to the options specified by {@code options}.
     *
     * @param amount the amount of content to retrieve
     * @param feedIds the IDs of the feeds to aggregate content from
     * @param aggregateCursor the aggregate {@link FeedCursor} representing the first piece of
     *                        content to start retrieving after for each feed
     * @param options the options dictating the final aggregation
     *
     * @return the aggregated content
     */
    public Flux<OneFeedContent> aggregate(
        int amount,
        List<FeedId> feedIds,
        FeedCursor aggregateCursor,
        AggregationOptions options
    ) {
        Map<FeedId, Integer> targetAmounts = options.getTargetAmounts(amount);

        Map<FeedId, FeedCursor> feedCursors = null;
        if (aggregateCursor != null) {
            feedCursors = Aggregation.decodeAggregateCursor(aggregateCursor);
        }
        List<Flux<OneFeedContent>> contentStreams = new ArrayList<>();

        for (FeedId feedId : feedIds) {
            Flux<OneFeedContent> contentStream;
            if (aggregateCursor == null || feedCursors.get(feedId) == null) {
                contentStream = feedService.getRecentContent(
                    feedId, targetAmounts.get(feedId));
            } else {
                contentStream = feedService.getRecentContent(
                    feedId, targetAmounts.get(feedId), feedCursors.get(feedId));
            }

            contentStreams.add(
                contentStream
                    .doOnError(err -> logger.warn(
                        "Error fetching content from feed '{}': {}", feedId, err.getStackTrace()))
                    .onErrorComplete()
            );
        }

        return Flux.merge(contentStreams);
    }
}
