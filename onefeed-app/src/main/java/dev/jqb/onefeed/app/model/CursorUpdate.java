package dev.jqb.onefeed.app.model;

import lombok.Getter;
import lombok.Setter;

/**
 * An aggregate cursor to send to clients upon completion of a content request
 */
@Getter
@Setter
public final class CursorUpdate extends StreamData {

    /**
     * The new aggregate cursor
     */
    private String aggregateCursor;

    /**
     * Constructs a new {@code CursorUpdate} with the given aggregate cursor.
     * @param aggregateCursor the new aggregate cursor
     */
    public CursorUpdate(String aggregateCursor) {
        super();
        this.aggregateCursor = aggregateCursor;
    }
}
