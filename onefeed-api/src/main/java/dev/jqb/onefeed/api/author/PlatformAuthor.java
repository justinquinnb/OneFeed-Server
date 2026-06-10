package dev.jqb.onefeed.api.author;

import dev.jqb.onefeed.api.feed.SourceInfo;
import dev.jqb.onefeed.api.provider.Provider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A marker class for {@link Provider}-specific author DTO types
 */
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public abstract non-sealed class PlatformAuthor extends Author {

    /**
     * Constructs a {@code PlatformAuthor} attributed to a {@code source} and represented by a
     * {@code handle}.
     *
     * @param source the origin of the author
     * @param handle the username of the author on the source's platform, devoid of any
     *                 platform-specific prefixes like {@code @}
     */
    public PlatformAuthor(SourceInfo source, String handle) {
        super(source, handle);
    }
}
