package dev.jqb.onefeed.api.aggregation;


import dev.jqb.onefeed.api.content.Content;
import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import java.util.Map;

/**
 * A type capable of generating and decoding aggregate nextPageCursors
 * @param <In> the type of {@link Content} required to generate the cursor with
 */
public interface AggregateCursorGenerator<In extends NormalizedContent> {
    /**
     * Generates an aggregate nextPageCursor from the cursors of the oldest content per feed.
     *
     * @param cursorMap a mapping of Feed ID strings to the cursor used to retrieve its next page
     *                  of content
     * @return the aggregate nextPageCursor, encoded in base 64
     */
    String generateAggregateCursor(Map<String, In> cursorMap);

    /**
     * Decodes an aggregate nextPageCursor into a map of Feed IDs to cursors.
     *
     * @param aggregateCursor the aggregate nextPageCursor, encoded in base 64, to decode
     * @return a mapping of feed ID strings to cursors
     * @see dev.jqb.onefeed.api.feed.FeedIdentifier#toIdString()
     */
    Map<String, String> decodeAggregateCursor(String aggregateCursor);
}
