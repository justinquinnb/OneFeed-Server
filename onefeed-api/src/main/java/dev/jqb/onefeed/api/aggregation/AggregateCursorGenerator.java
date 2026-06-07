package dev.jqb.onefeed.api.aggregation;


import dev.jqb.onefeed.api.content.Content;
import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import dev.jqb.onefeed.api.impl.OneFeedCursor;
import java.util.List;
import java.util.Map;

/**
 * A type capable of generating and decoding aggregate nextPageCursors
 * @param <In> the type of {@link Content} required to generate the cursor with
 */
public interface AggregateCursorGenerator<In extends NormalizedContent> {
    /**
     * Generates an aggregate cursor {@code String} from a list of {@code content}.
     *
     * @param content a list of the content to generate the cursor from
     * @return the aggregate nextPageCursor, encoded in base 64
     */
    String generateAggregateCursor(List<In> content);

    /**
     * Decodes an aggregate nextPageCursor into a map of Feed IDs to cursors.
     *
     * @param aggregateCursor the aggregate nextPageCursor, encoded in base 64, to decode
     * @return a mapping of feed ID strings to {@link OneFeedCursor}s
     * @see dev.jqb.onefeed.api.feed.FeedIdentifier#toIdString()
     */
    Map<FeedIdentifier, OneFeedCursor> decodeAggregateCursor(String aggregateCursor);
}
