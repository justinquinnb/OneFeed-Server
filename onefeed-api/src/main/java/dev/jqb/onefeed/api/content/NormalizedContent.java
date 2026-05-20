package dev.jqb.onefeed.api.content;

import dev.jqb.onefeed.api.feed.Provider;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link Provider}-generated content that has since been normalized,
 * making it ready for caching and distribution by OneFeed
 */
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public abstract class NormalizedContent extends Content {

    /**
     * Constructs a piece of {@code NormalizedContent} attributed to the provided {@code source}
     * and published at the given time.
     *
     * @param idOnPlatform the unique identifier of the content on its source platform
     * @param source the origin of the {@code Content}
     * @param published the time the {@code Content} was published on its {@code source}
     */
    public NormalizedContent(String idOnPlatform, Source source, Instant published) {
        super(idOnPlatform, source, published);
    }
}
