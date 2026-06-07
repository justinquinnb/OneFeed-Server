package dev.jqb.onefeed.api.feed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Information about a piece of content's author
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Author implements FeedIdentifiable {

    /**
     * The origin of the author
     */
    private SourceInfo source;

    /**
     * The handle of the author on the content's platform, devoid of any
     * platform-specific prefixes like {@code @}
     */
    private String handle;

    /**
     * Constructs a piece of {@code Author} info.
     *
     * @param source the origin of the author
     * @param handle the username of the author on the source's platform, devoid of any
     *                 platform-specific prefixes like {@code @}
     */
    public Author(SourceInfo source, String handle) {
        this.source = source;
        this.handle = handle;
    }

    @Override
    public FeedIdentifier getFeedIdentifier() {
        return source;
    }
}
