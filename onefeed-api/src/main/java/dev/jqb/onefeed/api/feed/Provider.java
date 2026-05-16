package dev.jqb.onefeed.api.feed;

import dev.jqb.onefeed.api.content.ContentFilter;
import dev.jqb.onefeed.api.content.Normalizer;
import dev.jqb.onefeed.api.content.RawContent;
import dev.jqb.onefeed.api.impl.OneFeedContent;
import java.util.HashMap;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * A provider of feed content via APIs
 *
 * @param <Out> the type of DTO that the provider produces
 */
public interface Provider<Out extends RawContent> {

    /**
     * Fetches the given {@code amount} of most recently published content from {@code this}
     * provider's content source for the given feed {@code name}.
     * 
     * @param name the name of the feed whose content to retrieve
     * @param amount the target amount of content to retrieve
     * @param filters the filters to try applying if supported by the API or best performed on the 
     *                {@link Out} content itself
     * @param config a map of feed-specific configuration options for this specific request
     *               
     * @return a {@link Mono} that emits a {@link FilteredContent} containing the retrieved content
     */
    Mono<FilteredContent<Out>> fetchRecentContent(
        String name,
        int amount,
        List<ContentFilter<?>> filters,
        HashMap<String, String> config
    );

    /**
     * Gets the {@link Normalizer} capable of transforming this provider's
     * {@link RawContent} DTO into normalized {@link OneFeedContent}
     *
     * @return a {@link Normalizer} capable of transforming this provider's
     * {@link RawContent} DTO into normalized {@link OneFeedContent}
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
