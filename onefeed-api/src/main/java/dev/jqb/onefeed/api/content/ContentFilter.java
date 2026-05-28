package dev.jqb.onefeed.api.content;

import java.util.List;

/**
 * A means of filtering provider content post-retrieval
 *
 * @param <T> the type of {@link Content} to filter
 */
public interface ContentFilter<T extends Content> {

    /**
     * Filters the given {@code content}.
     * @param content the content to filter
     * @return the filtered {@code content}
     */
    List<T> filter(List<T> content);
}
