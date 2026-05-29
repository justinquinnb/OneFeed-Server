package dev.jqb.onefeed.api.feed;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A minimum description of a platform entity's origin
 */
@Getter
@Setter
@ToString
public class SourceInfo {

    /**
     * The unique identifier of the feed the content is from
     */
    private FeedIdentifier feedId;

    /**
     * The unique identifier of the entity on its source platform
     */
    private String idOnPlatform;

    /**
     * The URL of the resource on its feed's platform
     */
    private String url;

    /**
     * Constructs a new {@code SourceInfo} bundle.
     *
     * @param feedId the provider-unique identifier of the feed the content is from
     * @param idOnPlatform the unique identifier of the content on its source platform
     * @param url the URL of the content resource on its feed's platform
     */
    public SourceInfo(FeedIdentifier feedId, String idOnPlatform, String url) {
        this.feedId = feedId;
        this.idOnPlatform = idOnPlatform;
        this.url = url;
    }
}
