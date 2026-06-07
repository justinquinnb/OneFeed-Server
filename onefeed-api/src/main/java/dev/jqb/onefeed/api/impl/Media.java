package dev.jqb.onefeed.api.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * A piece of media included in a piece of content
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Media {

    /**
     * The type of media being represented
     */
    private MediaType type;

    /**
     * The link to view the media on its host site
     */
    private String href;

    /**
     * The title or name of the media (such as the title of a link)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable
    private String title;

    /**
     * The media resource itself, for direct embedding
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String src;

    /**
     * The source of the resource to display, whether that be a link preview, video, image, etc.
     * Semantically dependent on the {@link #type} of media being represented.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable
    private String thumbnailSrc;

    /**
     * A caption for the piece of media
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String caption;

    /**
     * Alt text for the piece of media
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String altText;

    /**
     * Constructs a piece of {@link Media}.
     *
     * @param type the type of media the constructed object represents, guiding its fields' semantic
     *             interpretation or presentation by the client
     * @param href the click-through link to view the media on its host platform
     */
    public Media(MediaType type, String href) {
        this.type = type;
        this.href = href;
    }

    /**
     * A type of media, guiding the semantic interpretation or presentation of a {@link Media}
     * instance's field values.
     */
    public enum MediaType {
        /**
         * A hyperlink
         */
        LINK,

        /**
         * An image
         */
        IMAGE,

        /**
         * A video
         */
        VIDEO,

        /**
         * A document (e.g., a PDF, {@code .txt} file, or the like)
         */
        DOCUMENT
    }
}
