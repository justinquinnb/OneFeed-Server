package dev.jqb.onefeed.api.content;

import dev.jqb.onefeed.api.feed.FeedIdentifiable;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import dev.jqb.onefeed.api.feed.SourceInfo;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * The minimum required data for of a piece of content.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class Content implements FeedIdentifiable {

    /**
     * The origin of the content
     */
    protected SourceInfo source;

    /**
     * Gets time at which the content was published.
     */
    protected Instant published;

    /**
     * The cursor pointing to the next page of content after {@code this} (or some equivalent means),
     * if known, on the originating platform's API.
     */
    @Nullable
    protected String nextPageCursor;

    /**
     * Constructs a piece of {@code Content} attributed to a {@code source} and created/published
     * at the given time.
     *
     * @param source the origin of the content
     * @param nextPageCursor the cursor pointing to the next page of content after {@code this} (or
     *                       some equivalent means), if known, on the originating platform's API
     * @param published the time the {@code Content} was published on its {@code source}
     */
    public Content(SourceInfo source, @Nullable String nextPageCursor, Instant published) {
        this.source = source;
        this.nextPageCursor = nextPageCursor;
        this.published = published;
    }

    @Override
    public FeedIdentifier getFeedIdentifier() {
        return source.getFeedId();
    }
}
