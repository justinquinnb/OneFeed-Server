package dev.jqb.onefeedserver.exception;

import dev.jqb.onefeed.core.feed.FeedId;

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
    public UnknownFeedIdException(FeedId unknownId) {
        super(String.format("Unknown feed ID: %s", unknownId));
    }
}
