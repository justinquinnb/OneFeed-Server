package dev.jqb.onefeed.server.response.std;

/**
 * An {@link dev.jqb.onefeed.core.platform.ExternalRef} sent to a client of OneFeed
 *
 * @param url a direct URL to the resource
 * @param id the unique ID of the resource on its platform
 */
public record ExternalRefResponse(String url, String id) {

}
