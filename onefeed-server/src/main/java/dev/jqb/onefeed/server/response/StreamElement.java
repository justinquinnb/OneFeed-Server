package dev.jqb.onefeed.server.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.Instant;
import lombok.Getter;

/**
 * An element of a stream
 */
@Getter
@JsonPropertyOrder({"type", "data", "sentAt"})
public class StreamElement<T extends Streamable> {

    private final String type;
    private final T data;

    public StreamElement(T data) {
        this.type = data.getDisplayType();
        this.data = data;
    }

    @JsonProperty
    public Instant getSentAt() {
        return Instant.now();
    }
}
