package dev.jqb.onefeed.api.feed;

/**
 * A type whose source feed can be identified
 */
public interface FeedIdentifiable {

    /**
     * Gets the identifier of the feed the object is from
     * @return the identifier of the feed the object is from
     */
    FeedIdentifier getFeedIdentifier();
}
