package dev.jqb.onefeed.server.response.std;

import dev.jqb.onefeed.core.feed.FeedId;

/**
 * A {@link dev.jqb.onefeed.core.feed.FeedAttribution} sent to a client of OneFeed
 *
 * @param feedId the ID of the source feed
 * @param feedUrl the URL of the source feed
 */
public record FeedAttributionResponse(FeedId feedId, String feedUrl) {

}
