package dev.jqb.onefeed.api.feed;

import dev.jqb.onefeed.api.content.PlatformContent;
import lombok.Getter;

/**
 * A single feed of content from a single provider
 */
@Getter
public class Feed<Out extends PlatformContent> {

    /**
     * The unique identifier of the feed
     */
    private FeedIdentifier id;

    /**
     * The provider of the feed
     */
    private Provider<Out> provider;

    /**
     * Creates a new {@code Feed} of name {@code name} provided by the given {@code provider}.
     * @param id the unique identifier of the feed
     * @param provider the provider that feed is accessible via
     */
    public Feed(FeedIdentifier id, Provider<Out> provider) {
        this.id = id;
        this.provider = provider;
    }
}
