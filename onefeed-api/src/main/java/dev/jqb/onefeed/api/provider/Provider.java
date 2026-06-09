package dev.jqb.onefeed.api.provider;

import dev.jqb.onefeed.api.content.Normalizer;
import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.content.PlatformCursor;
import dev.jqb.onefeed.api.feed.Platform;
import dev.jqb.onefeed.api.impl.OneFeedContent;
import dev.jqb.onefeed.api.impl.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A provider of feed content via APIs
 *
 * @param <Out> the type of DTO that the provider produces
 */
public interface Provider<Out extends PlatformContent> {

    /**
     * Fetches the given {@code amount} of most recently published content from {@code this}
     * provider's content source for the given feed {@code name}.
     * 
     * @param feedName the name of the feed whose content to retrieve
     * @param amount the target amount of content to retrieve
     *
     * @return a {@link Flux} that emits a stream of {@link Out} containing at most the desired
     * {@code amount} of retrieved content
     */
    Flux<Out> fetchRecentContent(String feedName, int amount);

    /**
     * Fetches the given {@code amount} of most recently published content after the {@code cursor}
     * from {@code this} provider's content source for the given feed {@code name}.
     *
     * @param feedName the name of the feed whose content to retrieve
     * @param amount the target amount of content to retrieve
     * @param cursor the reference point to start retrieving content from, inclusive
     *
     * @return a {@link Flux} that emits a stream of {@link Out} containing at most the desired
     * {@code amount} of retrieved content
     */
    Flux<Out> fetchRecentContent(String feedName, int amount, PlatformCursor cursor);

    /**
     * Gets the {@link Normalizer} capable of transforming this provider's
     * {@link PlatformContent} DTO into normalized {@link OneFeedContent}
     *
     * @return a {@link Normalizer} capable of transforming this provider's {@link PlatformContent} DTO
     * into normalized {@link OneFeedContent}
     */
    Normalizer<Out, OneFeedContent> getNormalizer();

    /**
     * Gets info about this provider's source platform.
     * @return info about the source platform of this provider's content
     */
    Platform getPlatformInfo();

    /**
     * Gets the profile for the given feed.
     * @param feedName the name of the feed whose profile to retrieve
     * @return the profile for the given feed
     */
    Mono<Profile> getProfile(String feedName);
}
