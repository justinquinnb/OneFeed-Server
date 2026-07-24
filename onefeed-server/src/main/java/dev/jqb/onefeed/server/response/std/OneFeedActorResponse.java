package dev.jqb.onefeed.server.response.std;

import dev.jqb.onefeed.server.response.Streamable;

/**
 * A {@link dev.jqb.onefeed.core.actor.OneFeedActor} sent to a client of OneFeed
 *
 * @param providerId the unique identifier of the {@link dev.jqb.onefeed.core.provider.Provider}
 *                   the actor is from
 * @param externalRef a means of accessing the resource on the source platform
 * @param handle the username of the actor on the source's platform, devoid of any
 * @param name the non-unique, human name or nickname of the author
 * @param profilePicSrc a URL for their profile picture on the platform
 */
public record OneFeedActorResponse(
    String providerId,
    ExternalRefResponse externalRef,
    String handle,
    String name,
    String profilePicSrc
) implements Streamable {

    @Override
    public String getDisplayType() {
        return "ACTOR";
    }
}
