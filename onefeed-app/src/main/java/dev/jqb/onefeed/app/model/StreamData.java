package dev.jqb.onefeed.app.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StreamedContent.class, name = "CONTENT"),
    @JsonSubTypes.Type(value = StreamedAuthor.class, name = "AUTHOR"),
    @JsonSubTypes.Type(value = StreamedCursor.class, name = "CURSOR")
})
@Getter
@Setter
public sealed class StreamData permits StreamedAuthor, StreamedContent, StreamedCursor {

    /**
     * The time the data was sent to the client
     */
    private Instant sentAt;

    public StreamData() {
        this.sentAt = Instant.now();
    }
}
