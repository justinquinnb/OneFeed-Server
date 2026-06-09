package dev.jqb.onefeed.api.feed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A means of identifying a single feed of content
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class FeedIdentifier {

    /**
     * The unique identifier of the provider plugin exposing the feed
     */
    private String providerId;

    /**
     * The name of the feed as exposed by the provider
     */
    private String feedName;

    /**
     * Constructs a new {@code FeedIdentifier} for the given provider and feed name.
     * @param providerId the unique identifier of the provider plugin exposing the feed
     * @param feedName the name of the feed as exposed by the provider
     */
    public FeedIdentifier(String providerId, String feedName) {
        this.providerId = providerId;
        this.feedName = feedName;
    }

    /**
     * Converts this {@code FeedIdentifier} to a string suitable for use as a unique key
     * @return a string of format: {@code <}{@link #providerId}{@code >:<}{@link #feedName}{@code >}
     */
    @JsonValue
    public String toIdString() {
        return providerId + ":" + feedName;
    }

    /**
     * Converts a string of format {@code <}{@link #providerId}{@code >:<}{@link #feedName}{@code >}
     * into a {@code FeedIdentifier} object.
     * @param idString the string to convert
     * @return a {@code FeedIdentifier} object representing the given string
     */
    @JsonCreator
    public static FeedIdentifier fromIdString(String idString) {
        String[] parts = idString.split(":");

        if (parts.length != 2) {
            String reason = String.format("Expected 2 ':'-separated parts. Found %d", parts.length);
            throw new MalformedFeedIdException(idString, reason);
        }

        return new FeedIdentifier(parts[0], parts[1]);
    }
}
