package dev.jqb.onefeed.api.caching;

import dev.jqb.onefeed.api.author.NormalizedAuthor;
import dev.jqb.onefeed.api.content.ContentIdentifier;
import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import java.util.List;

/**
 * Provides a means of caching and retrieving {@link NormalizedContent} and
 * {@link NormalizedAuthor}s
 *
 * @param <C> the type of {@link NormalizedContent} in the cache
 * @param <A> the type of {@link NormalizedAuthor} in the cache
 */
public interface Cacher<C extends NormalizedContent, A extends NormalizedAuthor> {

    /**
     * Gets the {@code amount} most recent content from the cache.
     *
     * @param feed the feed whose content to retrieve
     * @param amount the amount of content to try retrieving
     *
     * @return at most {@code amount} pieces of cached content from the desired feed
     */
    List<C> fetchRecentContent(FeedIdentifier feed, int amount);

    /**
     * Gets the {@code amount} most recent content from the cache.
     *
     * @param feed the feed whose content to retrieve
     * @param amount the amount of content to try retrieving
     * @param after the reference point to start retrieving content after, exclusive
     *
     * @return at most {@code amount} pieces of cached content from the desired feed
     */
    List<C> fetchRecentContent(FeedIdentifier feed, int amount, ContentIdentifier after);

    /**
     * Gets a specific piece of content from the cache.
     * @param id the ID of the content to retrieve
     * @return the content with the given {@link ContentIdentifier}
     */
    C fetchContent(ContentIdentifier id);

    /**
     * Caches the given {@code content}.
     *
     * @param content the content to cache
     *
     * @implNote if the cache already contains a piece of content, update any changed fields
     * and its last updated timestamp
     */
    void cacheContent(List<C> content);

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
    A fetchAuthor(FeedIdentifier feed);

    /**
     * Caches the given {@code authors}.
     *
     * @param authors the author to cache
     *
     * @implNote if the cache already contains an author, update any changed fields
     * and its last updated timestamp
     */
    void cacheAuthors(List<A> authors);

    /**
     * Removes the author with the given id for the given feed from the cache.
     * @param feed the feed whose author to remove
     */
    void removeAuthor(FeedIdentifier feed);
}
