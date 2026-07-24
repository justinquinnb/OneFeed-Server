package dev.jqb.onefeed.server.response.std;

import dev.jqb.onefeed.core.actor.ActorKey;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * A complete aggregation of content, their authors, and (optionally) the platforms that they came
 * from
 *
 * @param authors the authors of the aggregated content, indexed by their {@link ActorKey}
 * @param platforms the platforms that the aggregated content came from, indexed by the ID of
 *                  the provider exposing them
 * @param content the aggregated content, in descending chronological order
 * @param aggregateCursor the cursor that can be used to retrieve the next batch of aggregated
 *                        content
 */
public record AggregationResponse(
    @Nullable Map<ActorKey, OneFeedActorResponse> authors,
    @Nullable Map<String, PlatformResponse> platforms,
    List<OneFeedContentResponse> content,
    @Nullable FeedCursorResponse aggregateCursor
) {}
