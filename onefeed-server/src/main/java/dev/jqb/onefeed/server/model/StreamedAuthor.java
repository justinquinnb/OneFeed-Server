package dev.jqb.onefeed.server.model;

import dev.jqb.onefeed.core.actor.Actor;
import lombok.Getter;
import lombok.Setter;

/**
 * A new author sent to a client of OneFeed
 */
@Getter
@Setter
public final class StreamedAuthor extends StreamData {

    /**
     * The unique identifier of the feed the author has, well... authored
     */
    private String feedId;

    /**
     * The author itself
     */
    private Actor author;

    /**
     * Constructs a new {@code AuthorUpdate} for the given author.
     * @param author the author to be sent to the client
     */
    public StreamedAuthor(Actor author) {
        super();
        this.author = author;
    }
}
