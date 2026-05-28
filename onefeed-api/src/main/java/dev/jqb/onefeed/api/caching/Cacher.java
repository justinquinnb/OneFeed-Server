package dev.jqb.onefeed.api.caching;

import dev.jqb.onefeed.api.content.Content;
import dev.jqb.onefeed.api.content.ContentFilter;
import dev.jqb.onefeed.api.feed.Author;
import dev.jqb.onefeed.api.feed.FeedInfo;
import dev.jqb.onefeed.api.feed.FilteredContent;
import java.time.Instant;
import java.util.List;

/**
 * Provides a means of caching and retrieving {@link Content} and{@link Author}s
 */
public interface Cacher {

    /**
     * Gets the {@code amount} most recent content from the cache.
     *
     * @param feed the feed whose content to retrieve
     * @param amount the amount of content to try retrieving
     *
     * @return at most {@code amount} pieces of cached content from the desired feed
     */
    FilteredContent<? extends Content> getMostRecentContent(FeedInfo feed, int amount, List<ContentFilter<? extends Content>> filters);

    /**
     * Gets the refresh timestamp of the content that hasn't been refreshed in the longest amount
     * of time (i.e., is the most stale).
     *
     * @param feed the feed whose data's freshness to check
     *
     * @return the refresh timestamp of the content in the desired {@code feed} that hasn't seen a
     * refresh in the longest amount of time
     *
     * @throws IllegalStateException if no content has been cached for the specified {@code feed}
     */
    Instant getStalestContentRefreshTime(FeedInfo feed) throws IllegalStateException;

    /**
     * Caches the given {@code content}.
     *
     * @param content the content to cache
     *
     * @implNote if the cache already contains a piece of content, update any changed fields
     * and its last updated timestamp
     */
    void cacheContent(List<CacheEntry<? extends Content>> content);

    /**
     * Gets the desired author from the cache
     *
     * @param feed the feed whose author to retrieve
     *
     * @return the author of the desired {@code feed}
     */
    Author getAuthor(FeedInfo feed);

    /**
     * Gets the refresh timestamp of the author of the desired {@code feed}.
     *
     * @param feed the feed whose author's data freshness to check
     *
     * @return the refresh timestamp of the author of the desired {@code feed}
     *
     * @throws IllegalStateException if no author has been cached for the specified {@code feed}
     */
    Instant getAuthorRefreshTime(FeedInfo feed) throws IllegalStateException;

    /**
     * Caches the given {@code authors}.
     *
     * @param authors the author to cache
     *
     * @implNote if the cache already contains an author, update any changed fields
     * and its last updated timestamp
     */
    void cacheAuthors(List<CacheEntry<? extends Author>> authors);
}
