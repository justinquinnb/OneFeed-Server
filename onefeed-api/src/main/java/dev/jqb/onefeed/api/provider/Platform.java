package dev.jqb.onefeed.api.provider;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Information about a content source, like Instagram
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Platform {

    /**
     * The name of the platform, like Instagram
     */
    private String name;

    /**
     * The URL of the platform's homepage
     */
    private String homepageUrl;

    /**
     * Constructs a piece of {@code Platform} info.
     *
     * @param name the name of the platform, like Instagram
     * @param homepageUrl a URL to the platform's homepage
     */
    public Platform(String name, String homepageUrl) {
        this.name = name;
        this.homepageUrl = homepageUrl;
    }
}
