package dev.jqb.onefeed.api.author;

import dev.jqb.onefeed.api.feed.FeedIdentifiable;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import dev.jqb.onefeed.api.feed.SourceInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The minimum required data for of a feed's author
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract sealed class Author implements FeedIdentifiable permits PlatformAuthor,
    NormalizedAuthor
{

    /**
     * The origin of the author
     */
    private SourceInfo source;

    /**
     * The handle of the author on the content's platform, devoid of any platform-specific prefixes
     * like {@code @}
     */
    private String handle;

    /**
     * Constructs an {@code Author} attributed to a {@code source} and represented by a
     * {@code handle}.
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
