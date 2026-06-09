package dev.jqb.onefeed.api.feed;

/**
 * Thrown when a feed identifier string is malformed
 *
 * @see FeedIdentifier#fromIdString(String)
 */
public class MalformedFeedIdException extends RuntimeException {

    /**
     * Creates a new {@code MalformedFeedIdException} with the given malformed ID and reason
     * @param malformedId the malformed ID string
     * @param reason the reason the ID is malformed
     */
    public MalformedFeedIdException(String malformedId, String reason) {
        super(String.format("Malformed feed ID string '%s': %s", malformedId, reason));
    }
}
