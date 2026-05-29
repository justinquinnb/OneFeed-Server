package dev.jqb.onefeed.api.impl;

import dev.jqb.onefeed.api.feed.Author;
import dev.jqb.onefeed.api.feed.SourceInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A feed author's profile
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Profile extends Author {

    /**
     * The non-unique, human name or nickname of the author
     */
    private String name;

    /**
     * The URL of their profile picture's source on the platform
     */
    private String profilePicSrc;

    /**
     * Constructs a user {@code Profile} object, effectively a more personalized piece of
     * {@link Author} info.
     *
     * @param source the origin of the profile
     * @param handle the handle of the profile on the source's platform, devoid of any
     *                 platform-specific prefixes like {@code @}
     * @param name the non-unique, human name or nickname of the author
     * @param profilePicSrc a URL for their profile picture on the platform
     */
    public Profile(SourceInfo source, String handle, String name, String profilePicSrc) {
        super(source, handle);
        this.name = name;
        this.profilePicSrc = profilePicSrc;
    }
}
