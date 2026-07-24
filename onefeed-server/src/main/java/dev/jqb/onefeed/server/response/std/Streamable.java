package dev.jqb.onefeed.server.response.std;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Instant;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = OneFeedContentResponse.class, name = "CONTENT"),
    @JsonSubTypes.Type(value = OneFeedActorResponse.class, name = "ACTOR"),
    @JsonSubTypes.Type(value = FeedCursorResponse.class, name = "CURSOR"),
    @JsonSubTypes.Type(value = PlatformResponse.class, name = "PLATFORM")
})
public sealed interface Streamable permits OneFeedActorResponse, OneFeedContentResponse,
    FeedCursorResponse, PlatformResponse
{
    default Instant getSentAt() {
        return Instant.now();
    }
}
