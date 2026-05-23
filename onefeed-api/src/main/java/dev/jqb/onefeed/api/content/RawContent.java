package dev.jqb.onefeed.api.content;

import dev.jqb.onefeed.api.feed.Provider;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A marker class for {@link Provider}-specific content DTO types
 */
@NoArgsConstructor
@Getter
@Setter
public abstract class RawContent extends Content {

    /**
     * Constructs a piece of {@code RawContent} attributed to a {@code source} and created/published
     * at the given time.
     *
     * @param idOnPlatform the unique identifier of the content on its source platform
     * @param cursor the cursor pointing to {@code this} content, or some equivalent means, on the
     *               originating platform's API
     * @param source the origin of the {@code Content}
     * @param published the time the {@code Content} was published on its {@code source}
     */
    public RawContent(String idOnPlatform, String cursor, Source source, Instant published) {
        super(idOnPlatform, cursor, source, published);
    }
}
