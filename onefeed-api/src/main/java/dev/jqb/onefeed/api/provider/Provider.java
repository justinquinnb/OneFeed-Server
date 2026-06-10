package dev.jqb.onefeed.api.provider;

import dev.jqb.onefeed.api.author.AuthorNormalizer;
import dev.jqb.onefeed.api.author.PlatformAuthor;
import dev.jqb.onefeed.api.content.ContentNormalizer;
import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.content.PlatformCursor;
import dev.jqb.onefeed.api.impl.OneFeedContent;
import dev.jqb.onefeed.api.impl.OneFeedAuthor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A provider of feed content via APIs
 *
 * @param <C> the type of {@link PlatformContent} DTO that the provider produces
 * @param <A> the type of {@link PlatformAuthor} DTO that the provider produces
 */
public interface Provider<C extends PlatformContent, A extends PlatformAuthor> {

    /**
     * Fetches the given {@code amount} of most recently published content from {@code this}
     * provider's content source for the given feed {@code name}.
     * 
     * @param feedName the name of the feed whose content to retrieve
     * @param amount the target amount of content to retrieve
     *
     * @return a {@link Flux} that emits a stream of {@link C} containing at most the desired
     * {@code amount} of retrieved content
     */
    Flux<C> fetchRecentContent(String feedName, int amount);

    /**
     * Fetches the given {@code amount} of most recently published content after the {@code cursor}
     * from {@code this} provider's content source for the given feed {@code name}.
     *
     * @param feedName the name of the feed whose content to retrieve
     * @param amount the target amount of content to retrieve
     * @param cursor the reference point to start retrieving content from, inclusive
     *
     * @return a {@link Flux} that emits a stream of {@link C} containing at most the desired
     * {@code amount} of retrieved content
     */
    Flux<C> fetchRecentContent(String feedName, int amount, PlatformCursor cursor);

    /**
     * Gets the {@link ContentNormalizer} capable of transforming this provider's
     * {@link PlatformContent} DTOs into normalized {@link OneFeedContent}
     *
     * @return a {@link ContentNormalizer} capable of transforming this provider's
     * {@link PlatformContent} DTO into normalized {@link OneFeedContent}
     */
    ContentNormalizer<C, OneFeedContent> getContentNormalizer();

    /**
     * Gets the {@link AuthorNormalizer} capable of transforming this provider's
     * {@link PlatformAuthor} DTOs into normalized {@link OneFeedAuthor}s
     *
     * @return a {@link AuthorNormalizer} capable of transforming this provider's
     * {@link PlatformAuthor} DTO into normalized {@link OneFeedAuthor}
     */
    AuthorNormalizer<A, OneFeedAuthor> getAuthorNormalizer();

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
    Mono<A> fetchAuthor(String feedName);
}
