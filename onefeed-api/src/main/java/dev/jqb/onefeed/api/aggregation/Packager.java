package dev.jqb.onefeed.api.aggregation;

import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.content.RawContent;
import dev.jqb.onefeed.api.feed.Author;
import dev.jqb.onefeed.api.feed.Feed;
import java.util.List;

/**
 * A type that can package aggregation data for efficient distribution
 */
public interface Packager<T extends NormalizedContent, U extends Author> {

    /**
     * Packages an aggregation of content from the given {@code feeds} into a {@link AggregationPackage}.
     *
     * @param amount the target amount of content to include (after all filters have been applied)
     * @param feeds the feeds to aggregate content from
     * @param options the options to adjust the contents of the returned aggregation
     * @param includeAuthors whether to include the authors of the aggregated content
     *
     * @return a list of all content that was retrieved, in descending order of creation timestamp
     */
    AggregationPackage<T, U> createPackage(int amount, List<Feed<? extends RawContent>> feeds,
        AggregationOptions options, boolean includeAuthors);
}
