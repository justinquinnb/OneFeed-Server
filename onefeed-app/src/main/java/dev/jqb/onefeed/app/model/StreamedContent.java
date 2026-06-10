package dev.jqb.onefeed.app.model;

import dev.jqb.onefeed.api.content.NormalizedContent;
import lombok.Getter;
import lombok.Setter;

/**
 * A new piece of content sent to a client of OneFeed
 * @param <T> the type of {@link NormalizedContent}
 */
@Getter
@Setter
public final class StreamedContent<T extends NormalizedContent> extends StreamData {

    /**
     * The new content
     */
    private T content;

    /**
     * Constructs a new {@code ContentUpdate} with the given {@code content}.
     * @param content the new content
     */
    public StreamedContent(T content) {
        super();
        this.content = content;
    }
}
