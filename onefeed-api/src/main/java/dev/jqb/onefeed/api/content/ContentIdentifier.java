package dev.jqb.onefeed.api.content;

import dev.jqb.onefeed.api.feed.FeedIdentifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A unique identifier for a piece of content in OneFeed
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ContentIdentifier extends FeedIdentifier {

    /**
     * The unique identifier of the entity on its source platform
     */
    private String idOnPlatform;

    /**
     * Constructs a new {@code ContentIdentifier} for the given provider ID, feed name, and
     * platform-specific content ID.
     *
     * @param providerId the unique identifier of the provider plugin exposing the feed
     * @param feedName the name of the feed as exposed by the provider
     * @param idOnPlatform the unique identifier of the content on its source platform
     */
    public ContentIdentifier(String providerId, String feedName, String idOnPlatform) {
        super(providerId, feedName);
        this.idOnPlatform = idOnPlatform;
    }
}
