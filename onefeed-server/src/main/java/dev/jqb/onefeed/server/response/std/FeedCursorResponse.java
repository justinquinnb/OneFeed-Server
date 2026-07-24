package dev.jqb.onefeed.server.response.std;

/**
 * A {@link dev.jqb.onefeed.core.feed.FeedCursor} to send to a client of OneFeed
 *
 * @param cursorOnPlatform the cursor, as provided by the platform's API
 * @param offsetFromCursor the distance from the cursor's item to the first item we desire from
 *                         the API. For example, if the cursor points to content piece 11, an
 *                         offset of 3 indicates content piece 14 should be the first piece to
 *                         consider.
 */
public record FeedCursorResponse(String cursorOnPlatform, int offsetFromCursor) implements Streamable {
}
