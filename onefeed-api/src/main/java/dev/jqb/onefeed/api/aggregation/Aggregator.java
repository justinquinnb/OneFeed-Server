package dev.jqb.onefeed.api.aggregation;

import dev.jqb.onefeed.api.content.ContentFilter;
import dev.jqb.onefeed.api.content.RawContent;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.Provider;
import java.util.HashMap;
import java.util.List;

/**
 * An aggregator of content across multiple {@link Provider}s
 */
public interface Aggregator {

    /**
     * Attempts to aggregate the given {@code amount} of content from the given {@code sources} with
     * the given {@code filters} applied.
     *
     * @param amount the target amount of content to return after all filters have been applied
     * @param feeds the feeds to aggregate content from
     * @param filters the filters to apply to the aggregated content, limiting the results
     * @param feedConfigs a map of feed names to {@link Provider}-specific configurations that should be
     *               applied
     *
     * @return a list of all content that was retrieved, in descending order of creation timestamp
     */
    List<RawContent> aggregate(int amount, List<Feed<?>> feeds,
        List<ContentFilter<?>> filters, HashMap<String, String> feedConfigs);

    /**
     * Attempts to aggregate the given {@code amount} of content from the given {@code sources} with
     * the given {@code filters} applied.
     *
     * @param amount the target amount of content to return after all filters have been applied
     * @param feeds the feeds to aggregate content from
     * @param aggregateCursor the OneFeed-generated nextPageCursor to derive feed-specific cursors from
     * @param filters the filters to apply to the aggregated content, limiting the results
     * @param feedConfigs a map of feed names to {@link Provider}-specific configurations that should be
     *               applied
     *
     * @return a list of all content that was retrieved, in descending order of creation timestamp
     */
    List<RawContent> aggregate(int amount, List<Feed<?>> feeds, String aggregateCursor,
        List<ContentFilter<?>> filters, HashMap<String, String> feedConfigs);

    /**
     * Generates an aggregate nextPageCursor from the cursors of the oldest content per feed.
     *
     * @param content the content to generate an aggregate nextPageCursor for
     * @return the aggregate nextPageCursor, encoded in base 64
     */
    String generateAggregateCursor(List<RawContent> content);

    /**
     * Decodes an aggregate nextPageCursor into a map of feed names to cursors.
     *
     * @param aggregateCursor the aggregate nextPageCursor, encoded in base 64, to decode
     * @return a mapping of feed names to cursors
     */
    HashMap<String, String> decodeAggregateCursor(String aggregateCursor);
}
