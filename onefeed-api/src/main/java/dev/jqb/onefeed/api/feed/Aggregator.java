package dev.jqb.onefeed.api.feed;

import dev.jqb.onefeed.api.content.ContentFilter;
import dev.jqb.onefeed.api.content.NormalizedContent;
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
    List<NormalizedContent> aggregate(int amount, List<Feed<?>> feeds,
        List<ContentFilter<?>> filters, HashMap<String, String> feedConfigs);
}
