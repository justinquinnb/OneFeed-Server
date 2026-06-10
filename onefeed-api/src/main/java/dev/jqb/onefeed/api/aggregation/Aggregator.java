package dev.jqb.onefeed.api.aggregation;

import dev.jqb.onefeed.api.author.PlatformAuthor;
import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.content.PlatformCursor;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import dev.jqb.onefeed.api.provider.Provider;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Flux;

/**
 * An aggregator of content across multiple {@link Provider}s
 */
public interface Aggregator<Out extends NormalizedContent> {

    /**
     * Attempts to aggregate the given {@code amount} of content from the given {@code sources} with
     * the given {@code filters} applied.
     *
     * @param amount the target amount of content to return after all filters have been applied
     * @param feeds the feeds to aggregate content from
     * @param options the options to adjust the contents of the returned aggregation
     *
     * @return a list of all content that was retrieved, not necessarily in order
     */
    Flux<Out> aggregate(
        int amount,
        List<Feed<? extends PlatformContent, ? extends PlatformAuthor>> feeds,
        AggregationOptions options
    );

    /**
     * Attempts to aggregate the given {@code amount} of content from the given {@code sources} with
     * the given {@code filters} applied.
     *
     * @param amount the target amount of content to return after all filters have been applied
     * @param feeds the feeds to aggregate content from
     * @param cursors a mapping of Feed IDs to feed-specific cursors
     * @param options the options to adjust the contents of the returned aggregation
     *
     * @return a list of all content that was retrieved, not necessarily in order
     */
    Flux<Out> aggregate(
        int amount,
        List<Feed<? extends PlatformContent, ? extends PlatformAuthor>> feeds,
        Map<FeedIdentifier, ? extends PlatformCursor> cursors,
        AggregationOptions options
    );
}
