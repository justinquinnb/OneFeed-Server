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
public class Author {

    /**
     * The unique, permanent identifier of the author on the content's platform
     */
    private String id;

    /**
     * The handle of the author on the content's platform, devoid of any
     * platform-specific prefixes like {@code @}
     */
    private String handle;

    /**
     * The URL of the author's feed on the content's platform
     */
    private String feedUrl;

    /**
     * Constructs a piece of {@code Author} info.
     *
     * @param id the unique, permanent identifier of the author on the source's platform
     * @param handle the username of the author on the source's platform, devoid of any
     *                 platform-specific prefixes like {@code @}
     * @param feedUrl the URL of the author's feed on the source's platform
     */
    public Author(String id, String handle, String feedUrl) {
        this.id = id;
        this.handle = handle;
        this.feedUrl = feedUrl;
    }
}
