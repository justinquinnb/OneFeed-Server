package dev.jqb.onefeed.api.aggregation;

import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.content.RawContent;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.Provider;
import java.util.HashMap;
import java.util.List;
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
     * @return a list of all content that was retrieved, in descending order of creation timestamp
     */
    Flux<Out> aggregate(int amount, List<Feed<? extends RawContent>> feeds,
        AggregationOptions options);

    /**
     * Attempts to aggregate the given {@code amount} of content from the given {@code sources} with
     * the given {@code filters} applied.
     *
     * @param amount the target amount of content to return after all filters have been applied
     * @param feeds the feeds to aggregate content from
     * @param aggregateCursor the OneFeed-generated nextPageCursor to derive feed-specific cursors from
     * @param options the options to adjust the contents of the returned aggregation
     *
     * @return a list of all content that was retrieved, in descending order of creation timestamp
     */
    Flux<Out> aggregate(int amount, List<Feed<? extends RawContent>> feeds,
        String aggregateCursor, AggregationOptions options);

    /**
     * Generates an aggregate nextPageCursor from the cursors of the oldest content per feed.
     *
     * @param content the content to generate an aggregate nextPageCursor for
     * @return the aggregate nextPageCursor, encoded in base 64
     */
    String generateAggregateCursor(List<Out> content);

    /**
     * Decodes an aggregate nextPageCursor into a map of feed names to cursors.
     *
     * @param aggregateCursor the aggregate nextPageCursor, encoded in base 64, to decode
     * @return a mapping of feed ID strings to cursors
     * @see dev.jqb.onefeed.api.feed.FeedIdentifier#toIdString()
     */
    HashMap<String, String> decodeAggregateCursor(String aggregateCursor);
}
