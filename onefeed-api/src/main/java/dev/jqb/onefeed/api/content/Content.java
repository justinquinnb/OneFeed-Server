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
@ToString
@NoArgsConstructor
public abstract sealed class Content implements FeedIdentifiable, Comparable<Content>
    permits PlatformContent, NormalizedContent
{

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

    /**
     * Compares this {@code Content} to another {@code Content} by their published time, producing
     * a descending, chronological order appropriate for feeds.
     *
     * @param other the other piece of content to compare to
     * @return the comparator value, that is less than zero if {@code other.published} time is
     * before this {@code published} time, zero if they are equal, or greater than zero if
     * {@code other.published} is after  this {@code published} time
     */
    @Override
    public int compareTo(Content other) {
        return other.published.compareTo(published);
    }

    @Override
    public FeedIdentifier getFeedIdentifier() {
        return source;
    }
}
