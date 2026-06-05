package dev.jqb.onefeed.api.caching;

import dev.jqb.onefeed.api.content.Content;
import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.feed.Author;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import java.util.List;

/**
 * Provides a means of caching and retrieving {@link Content} and{@link Author}s
 *
 * @param <T> the type of {@link Content} in the cache
 * @param <U> the type of {@link Author} in the cache
 */
public interface Cacher<T extends NormalizedContent, U extends Author> {

    /**
     * Gets the {@code amount} most recent content from the cache.
     *
     * @param feed the feed whose content to retrieve
     * @param amount the amount of content to try retrieving
     *
     * @return at most {@code amount} pieces of cached content from the desired feed
     */
    List<T> fetchRecentContent(FeedIdentifier feed, int amount);

    /**
     * Gets the {@code amount} most recent content from the cache.
     *
     * @param feed the feed whose content to retrieve
     * @param amount the amount of content to try retrieving
     * @param cursor the point to start retrieving content after, inclusively
     *
     * @return at most {@code amount} pieces of cached content from the desired feed
     */
    List<T> fetchRecentContent(FeedIdentifier feed, int amount, String cursor);

    /**
     * Gets a specific piece of content from the cache.
     * @param feed the feed whose content to retrieve
     * @param idOnPlatform the id of the content to retrieve
     * @return the content with the given id for the given {@code feed}, or {@code null} if no such
     * content exists
     */
    T fetchContent(FeedIdentifier feed, String idOnPlatform);

    /**
     * Caches the given {@code content}.
     *
     * @param content the content to cache
     *
     * @implNote if the cache already contains a piece of content, update any changed fields
     * and its last updated timestamp
     */
    void cacheContent(List<T> content);

    /**
     * Removes the content with the given id for the given feed from the cache.
     * @param feed the feed whose content to remove
     * @param idOnPlatform the id of the content to remove
     */
    void removeContent(FeedIdentifier feed, String idOnPlatform);

    /**
     * Gets the desired author from the cache
     *
     * @param feed the feed whose author to retrieve
     *
     * @return the author of the desired {@code feed}
     */
    U fetchAuthor(FeedIdentifier feed);

    /**
     * Caches the given {@code authors}.
     *
     * @param authors the author to cache
     *
     * @implNote if the cache already contains an author, update any changed fields
     * and its last updated timestamp
     */
    void cacheAuthors(List<U> authors);

    /**
     * Removes the author with the given id for the given feed from the cache.
     * @param feed the feed whose author to remove
     */
    void removeAuthor(FeedIdentifier feed);
}
