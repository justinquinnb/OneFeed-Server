package dev.jqb.onefeed.api.content;

import dev.jqb.onefeed.api.feed.Provider;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

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
     * Constructs a piece of {@code NormalizedContent} attributed to a {@code source} and
     * created/published at the given time.
     *
     * @param source the origin of the content
     * @param nextPageCursor the cursor pointing to the next page of content after {@code this} (or
     *                       some equivalent means), if known, on the originating platform's API
     * @param published      the time the {@code Content} was published on its {@code source}
     */
    public NormalizedContent(SourceInfo source, @Nullable String nextPageCursor, Instant published) {
        super(source, nextPageCursor, published);
    }
}
