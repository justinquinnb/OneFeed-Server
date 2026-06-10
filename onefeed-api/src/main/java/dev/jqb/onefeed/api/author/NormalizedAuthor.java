package dev.jqb.onefeed.api.author;

import dev.jqb.onefeed.api.feed.SourceInfo;
import dev.jqb.onefeed.api.provider.Provider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A {@link Provider}-generated author that has since been normalized, making it ready for caching
 * and distribution by OneFeed
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public abstract non-sealed class NormalizedAuthor extends Author {

    /**
     * Constructs a {@code NormalizedAuthor} attributed to a {@code source} and represented by a
     * {@code handle}.
     *
     * @param source the origin of the author
     * @param handle the username of the author on the source's platform, devoid of any
     *                 platform-specific prefixes like {@code @}
     */
    public NormalizedAuthor(SourceInfo source, String handle) {
        super(source, handle);
    }
}
