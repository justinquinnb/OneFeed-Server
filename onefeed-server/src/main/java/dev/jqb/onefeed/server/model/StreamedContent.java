package dev.jqb.onefeed.server.model;

import dev.jqb.onefeed.core.content.Content;
import lombok.Getter;
import lombok.Setter;

/**
 * A new piece of content sent to a client of OneFeed
 */
@Getter
@Setter
public final class StreamedContent extends StreamData {

    /**
     * The new content
     */
    private Content content;

    /**
     * Constructs a new {@code ContentUpdate} with the given {@code content}.
     * @param content the new content
     */
    public StreamedContent(Content content) {
        super();
        this.content = content;
    }
}
