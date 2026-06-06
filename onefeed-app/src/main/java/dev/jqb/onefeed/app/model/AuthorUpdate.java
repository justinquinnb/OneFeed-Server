package dev.jqb.onefeed.app.model;

import dev.jqb.onefeed.api.feed.Author;
import lombok.Getter;
import lombok.Setter;

/**
 * A new author sent to a client of OneFeed
 */
@Getter
@Setter
public final class AuthorUpdate implements StreamData {

    /**
     * The unique identifier of the feed the author has, well... authored
     */
    private String feedId;

    /**
     * The author itself
     */
    private Author author;

    /**
     * Constructs a new {@code AuthorUpdate} for the given author.
     * @param author the author to be sent to the client
     */
    public AuthorUpdate(Author author) {
        this.author = author;
    }
}
