package dev.jqb.onefeedserver.cacherpluginsdk;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.actor.ActorKey;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.content.ContentKey;
import dev.jqb.onefeed.core.feed.FeedCursor;
import dev.jqb.onefeed.core.feed.FeedId;
import java.util.List;

/**
 * Provides a means of caching and retrieving {@link Content} and
 * {@link Actor}s
 *
 * @param <C> the type of {@link Content} in the cache
 * @param <A> the type of {@link Actor} in the cache
 */
public interface Cacher<C extends Content, A extends Actor> {

    /**
     * Gets the {@code amount} most recent content from the cache.
     *
     * @param feed the feed whose content to retrieve
     * @param amount the amount of content to try retrieving
     *
     * @return at most {@code amount} pieces of cached content from the desired feed
     */
    List<C> fetchRecentContent(FeedId feed, int amount);

    /**
     * Gets the {@code amount} most recent content from the cache after a specific piece of feed's
     * content.
     *
     * @param feed the feed whose content to retrieve
     * @param amount the amount of content to try retrieving
     * @param cursor the reference point to retrieve content after, inclusive
     *
     * @return at most {@code amount} pieces of cached content from the desired feed
     */
    List<C> fetchRecentContent(FeedId feed, int amount, FeedCursor cursor);

    /**
     * Gets a specific piece of content from the cache.
     * @param key the unique identifier of the content to retrieve
     * @return the content with the given {@link ContentKey}
     */
    C fetchContent(ContentKey key);

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
     * Removes the content with the given key for the given feed from the cache.
     * @param key the unique identifier of the content to retrieve
     */
    void removeContent(ContentKey key);

    /**
     * Gets the desired author from the cache
     *
     * @param key the key of the author to retrieve
     *
     * @return the author of the desired {@code feed}
     */
    A fetchAuthor(ActorKey key);

    /**
     * Caches the given {@code authors}.
     *
     * @param authors the author to cache
     *
     * @implNote if the cache already contains an author, update any changed fields
     * and its last updated timestamp
     */
    void cacheAuthor(A authors);

    /**
     * Removes the author with the given id for the given feed from the cache.
     * @param key the key of the author to remove
     */
    void removeAuthor(ActorKey key);
}
