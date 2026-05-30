package dev.jqb.onefeed.api.feed;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A means of identifying a single feed of content
 */
@Getter
@Setter
@ToString
public class FeedIdentifier {

    /**
     * The unique identifier of the provider plugin exposing the feed
     */
    private String providerId;

    /**
     * The name of the feed as exposed by the provider
     */
    private String name;

    /**
     * Constructs a new {@code FeedIdentifier} for the given provider and feed name.
     * @param providerId the unique identifier of the provider plugin exposing the feed
     * @param name the name of the feed as exposed by the provider
     */
    public FeedIdentifier(String providerId, String name) {
        this.providerId = providerId;
        this.name = name;
    }

    /**
     * Converts this {@code FeedIdentifier} to a string suitable for use as a unique key
     * @return a string of format: {@code <}{@link #providerId}{@code >:<}{@link #name}{@code >}
     */
    public String toIdString() {
        return providerId + ":" + name;
    }

    /**
     * Converts a string of format {@code <}{@link #providerId}{@code >:<}{@link #name}{@code >}
     * into a {@code FeedIdentifier} object.
     * @param idString the string to convert
     * @return a {@code FeedIdentifier} object representing the given string
     */
    public static FeedIdentifier fromIdString(String idString) {
        String[] parts = idString.split(":");
        return new FeedIdentifier(parts[0], parts[1]);
    }
}
