package dev.jqb.onefeed.api.caching;

import java.time.Instant;
import lombok.Getter;

/**
 * An entry to some cache for an object of type {@code T}
 *
 * @param <T> the type of object being cached
 */
@Getter
public class CacheEntry<T> {

    /**
     * The actual data being cached
     */
    public T data;

    /**
     * The last time the data the cache entry is for has been retrieved from its source. An
     * indicator of data staleness.
     */
    public Instant lastRetrieved;

    /**
     * The Hard TTL cutoff to prevent hoarding objects in the cache
     */
    public Instant expireOn;

    /**
     * Constructs a new {@code CacheEntry} container for the given {@code data}.
     *
     * @param data the data for the created {@code CacheEntry} to contain
     * @param lastRetrieved the last time the contained data has been retrieved from its source
     * @param expireOn the moment the data can be considered "expired" and ready for removal from
     *                 the cache
     */
    public CacheEntry(T data, Instant lastRetrieved, Instant expireOn) {
        this.data = data;
        this.lastRetrieved = lastRetrieved;
        this.expireOn = expireOn;
    }
}
