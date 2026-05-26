package dev.jqb.onefeed.api.content;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The minimum required data for of a piece of content. This is an interface so
 * DTOs from content providers do not have to implement specific fields when
 * deserializing API responses.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class Content {

    /**
     * The unique identifier of the content on its source platform.
     */
    protected String idOnPlatform;

    /**
     * Gets information about the source of the content.
     */
    protected Source source;

    /**
     * Gets time at which the content was published.
     */
    protected Instant published;

    /**
     * The nextPageCursor pointing to {@code this} content, or some equivalent means, on the originating
     * platform's API.
     */
    protected String nextPageCursor;

    /**
     * Constructs a piece of {@code Content} attributed to a {@code source} and created/published
     * at the given time.
     *
     * @param idOnPlatform the unique identifier of the content on its source platform
     * @param nextPageCursor the nextPageCursor pointing to {@code this} content, or some equivalent means, on the
     *               originating platform's API
     * @param source the origin of the {@code Content}
     * @param published the time the {@code Content} was published on its {@code source}
     */
    public Content(String idOnPlatform, String nextPageCursor, Source source, Instant published) {
        this.idOnPlatform = idOnPlatform;
        this.nextPageCursor = nextPageCursor;
        this.source = source;
        this.published = published;
    }
}
