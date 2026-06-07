package dev.jqb.onefeed.api.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A platform-specific cursor and offset to identify the next piece of content desired from the API
 * when the adjacent, prior content's cursor is possibly unknown
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PlatformCursor {

    /**
     * The cursor, as provided by the platform's API
     */
    private String cursorOnPlatform;

    /**
     * The distance from the cursor's item to the first item we desire from the API. </br></br>
     * For example, if the cursor points to content piece 11, an offset of 3 indicates content piece
     * 14 should be the first piece to consider.
     */
    private int offsetFromCursor;

    /**
     * Constructs a {@code PlatformCursor} with the given cursor and offset.
     *
     * @param cursorOnPlatform the cursor, as provided by the platform's API
     * @param offsetFromCursor the distance from the cursor's item to the first item we desire from
     *                         the API. For example, if the cursor points to content piece 11, an
     *                         offset of 3 indicates content piece 14 should be the first piece to
     *                         consider.
     */
    public PlatformCursor(String cursorOnPlatform, int offsetFromCursor) {
        this.cursorOnPlatform = cursorOnPlatform;
        this.offsetFromCursor = offsetFromCursor;
    }
}
