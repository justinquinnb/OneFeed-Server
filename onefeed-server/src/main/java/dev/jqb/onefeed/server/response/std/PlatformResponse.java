package dev.jqb.onefeed.server.response.std;

import dev.jqb.onefeed.server.response.Streamable;

/**
 * A {@link dev.jqb.onefeed.core.platform.Platform} sent to a client of OneFeed
 *
 * @param providerId the unique identifier of the provider exposing this platform
 * @param name the name of the platform, like Instagram
 * @param homepageUrl a URL to the platform's homepage
 */
public record PlatformResponse(
    String providerId,
    String name,
    String homepageUrl
) implements Streamable {

    @Override
    public String getDisplayType() {
        return "PLATFORM";
    }
}
