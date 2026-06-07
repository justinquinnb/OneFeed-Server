package dev.jqb.onefeed.api.impl;

import dev.jqb.onefeed.api.content.PlatformCursor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A cursor that combines cache-compatible content identification with a platform-specific feed page
 * cursor
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OneFeedCursor extends PlatformCursor {

    /**
     * The ID of the content on its source platform
     */
    private String idOnPlatform;

    /**
     * Constructs a {@code CombinedCursor} with the given ID and cursor.
     *
     * @param cursorOnPlatform the cursor, as provided by the platform's API
     * @param offsetFromCursor the distance from the cursor's item to the first item we desire from
     *                         the API. For example, if the cursor points to content piece 11, an
     *                         offset of 3 indicates content piece 14 should be the first piece to
     *                         consider.
     * @param idOnPlatform the ID of the content on its source platform
     */
    public OneFeedCursor(String cursorOnPlatform, int offsetFromCursor, String idOnPlatform) {
        super(cursorOnPlatform, offsetFromCursor);
        this.idOnPlatform = idOnPlatform;
    }
}
