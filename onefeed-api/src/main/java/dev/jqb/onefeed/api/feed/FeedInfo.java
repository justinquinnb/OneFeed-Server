package dev.jqb.onefeed.api.feed;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Information about a single feed of content
 */
@Getter
@Setter
@ToString(callSuper = true)
public class FeedInfo extends FeedIdentifier {

    /**
     * The platform that the content is hosted on/comes from/has been posted to
     */
    private Platform platform;

    /**
     * The author of the content
     */
    private Author author;

    /**
     * Creates a new {@code FeedInfo} bundle for the given {@code platform}, {@code author} and
     * {@code providerId}-{@code name} ID pair.
     *
     * @param providerId the unique identifier of the provider plugin exposing the feed
     * @param name the name of the feed as exposed by the provider
     */
    public FeedInfo(String providerId, String name, Platform platform, Author author) {
        super(providerId, name);
        this.platform = platform;
        this.author = author;
    }
}
