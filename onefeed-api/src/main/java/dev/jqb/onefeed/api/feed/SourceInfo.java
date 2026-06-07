package dev.jqb.onefeed.api.feed;

import dev.jqb.onefeed.api.content.ContentIdentifier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A complete identifier for a piece of content on a feed from a platform's provider in OneFeed
 */
@Getter
@Setter
@ToString(callSuper = true)
public class SourceInfo extends ContentIdentifier {

    /**
     * The URL of the resource on its feed's platform
     */
    private final String urlOnPlatform;

    /**
     * Constructs a new {@code ContentIdentifier} for the given provider ID, feed name, and
     * platform-specific content ID and source URL.
     *
     * @param providerId the unique identifier of the provider plugin exposing the feed
     * @param feedName the name of the feed as exposed by the provider
     * @param idOnPlatform the unique identifier of the content on its source platform
     * @param urlOnPlatform the URL of the content on its source platform
     */
    public SourceInfo(String providerId, String feedName, String idOnPlatform, String urlOnPlatform) {
        super(providerId, feedName, idOnPlatform);
        this.urlOnPlatform = urlOnPlatform;
    }
}
