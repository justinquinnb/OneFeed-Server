package dev.jqb.onefeed.api.feed;

import dev.jqb.onefeed.api.content.RawContent;
import lombok.Getter;

/**
 * A single feed of content from a single provider
 */
@Getter
public class Feed<Out extends RawContent> {

    /**
     * The name of the feed
     */
    private String name;

    /**
     * The provider of the feed
     */
    private Provider<Out> provider;

    /**
     * Creates a new {@code Feed} of name {@code name} provided by the given {@code provider}.
     * @param name the name of the feed
     * @param provider the provider that feed is accessible via
     */
    public Feed(String name, Provider<Out> provider) {
        this.name = name;
        this.provider = provider;
    }
}
