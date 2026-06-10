package dev.jqb.onefeed.api.feed;

/**
 * Thrown when a feed ID cannot be found in OneFeed's feed registry
 */
public class UnknownFeedIdException extends RuntimeException {

    /**
     * Creates a new {@code UnknownFeedIdException} with the given ID
     * @param unknownId the ID of the feed that was not found
     */
    public UnknownFeedIdException(String unknownId) {
        super(String.format("Unknown feed ID: %s", unknownId));
    }

    /**
     * Creates a new {@code UnknownFeedIdException} with the given ID
     * @param unknownId the ID of the feed that was not found
     */
    public UnknownFeedIdException(FeedIdentifier unknownId) {
        super(String.format("Unknown feed ID: %s", unknownId.toIdString()));
    }
}
