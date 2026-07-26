package dev.jqb.onefeedserver.response.std;

import dev.jqb.onefeed.core.actor.ActorKey;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * A complete collection of content, their authors, and (optionally) the platform that it came from
 *
 * @param authors the authors of the content, indexed by their {@link ActorKey}
 * @param platform the platform that the content came from
 * @param content the content, in descending chronological order
 * @param cursor the cursor that can be used to retrieve the next batch of content
 */
public record FeedResponse(
    Map<ActorKey, OneFeedActorResponse> authors,
    @Nullable PlatformResponse platform,
    List<OneFeedContentResponse> content,
    @Nullable FeedCursorResponse cursor
) {

}
