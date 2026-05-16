package dev.jqb.onefeed.api.feed;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Information about a single feed of content
 *
 */
@Getter
@Setter
@ToString
public class FeedInfo {

    /**
     * The unique identifier of the provider exposing the feed
     */
    private String providerId;

    /**
     * The name of the feed
     */
    private String name;

    /**
     * The platform that the content is hosted on/comes from/has been posted to
     */
    private Platform platform;

    /**
     * The author of the content
     */
    private Author author;

    /**
     * Creates a new {@code Feed} for the given {@code provider} and feed {@code name}.
     *
     * @param providerId the unique identifier of the provider exposing the feed
     * @param name the name of the feed
     * @param platform the platform the content is hosted on/comes from/has been posted to
     * @param author the author of the content
     */
    public FeedInfo(String providerId, String name, Platform platform, Author author) {
        this.providerId = providerId;
        this.name = name;
        this.platform = platform;
        this.author = author;
    }
}
