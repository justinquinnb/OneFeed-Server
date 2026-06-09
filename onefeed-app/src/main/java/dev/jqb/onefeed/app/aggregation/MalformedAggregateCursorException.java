package dev.jqb.onefeed.app.aggregation;

/**
 * Thrown when an aggregate cursor is malformed
 */
public class MalformedAggregateCursorException extends RuntimeException {

    /**
     * Creates a new {@code MalformedAggregateCursorException}
     */
    public MalformedAggregateCursorException() {
        super("Aggregate cursor is malformed.");
    }
}
