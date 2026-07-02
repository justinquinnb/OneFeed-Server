package dev.jqb.onefeed.server.model;

import dev.jqb.onefeed.core.feed.FeedCursor;
import lombok.Getter;
import lombok.Setter;

/**
 * An aggregate cursor to send to clients upon completion of a content request
 */
@Getter
@Setter
public final class StreamedCursor extends StreamData {

    /**
     * The new aggregate cursor
     */
    private FeedCursor aggregateCursor;

    /**
     * Constructs a new {@code CursorUpdate} with the given aggregate cursor.
     * @param aggregateCursor the new aggregate cursor
     */
    public StreamedCursor(FeedCursor aggregateCursor) {
        super();
        this.aggregateCursor = aggregateCursor;
    }
}
