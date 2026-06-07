package dev.jqb.onefeed.api.feed;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A type whose source feed can be identified
 */
public interface FeedIdentifiable {

    /**
     * Gets the identifier of the feed the object is from
     * @return the identifier of the feed the object is from
     */
    @JsonIgnore
    FeedIdentifier getFeedIdentifier();
}
