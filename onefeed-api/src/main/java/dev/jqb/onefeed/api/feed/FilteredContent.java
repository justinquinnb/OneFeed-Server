package dev.jqb.onefeed.api.feed;

import dev.jqb.onefeed.api.content.Content;
import dev.jqb.onefeed.api.content.ContentFilter;
import dev.jqb.onefeed.api.content.ContentPackage;
import dev.jqb.onefeed.api.content.RawContent;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * A response from a {@link Provider} containg retrieved,
 * {@link RawContent} and metadata about it/the retrieval
 *
 * @param <Out> the type of {@link RawContent} contained in this response
 */
@Getter
@Setter
public class FilteredContent<Out extends Content> extends ContentPackage<Out> {
    /**
     * The {@link ContentFilter}s that were used to derive the content
     */
    private List<ContentFilter<Out>> appliedFilters;

    /**
     * Constructs a {@code ProviderResponse} with the provided {@code content}, the timestamp it was
     * generated at, and the filters that were applied to derive the enclosed list of
     * {@code content}.
     *
     * @param content the content returned by the provider after having been filtered by the
     *                {@code appliedFilters}
     * @param generated the timestamp the {@code content} was generated at
     * @param appliedFilters the {@link ContentFilter}s that were used to derive that final list of
     *                       {@code content}
     */
    public FilteredContent(List<Out> content, Instant generated,
        List<ContentFilter<Out>> appliedFilters
    ) {
        super(content, generated);
        this.appliedFilters = appliedFilters;
    }

}
