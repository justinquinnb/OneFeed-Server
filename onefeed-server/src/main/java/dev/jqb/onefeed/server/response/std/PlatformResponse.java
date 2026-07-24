package dev.jqb.onefeed.server.response.std;

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

}
