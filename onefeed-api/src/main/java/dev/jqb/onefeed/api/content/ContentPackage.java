package dev.jqb.onefeed.api.content;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * A package of content associated with the timestamp it was generated at.
 *
 * @param <Out> the type of {@link RawContent} contained in this response
 */
@Getter
@Setter
public class ContentPackage<Out extends Content> {
    /**
     * The requested content, normalized and ready for distribution
     */
    private List<Out> content;

    /**
     * The time at which {@code this} package was generated
     */
    private Instant generated;

    /**
     * Constructs a {@code ContentPackage} from the given {@code content}, generated at the provided
     * timestamp.
     *
     * @param content the batch of content generated at the given timestamp
     * @param generated the timestamp the {@code content} was generated at
     */
    public ContentPackage(List<Out> content, Instant generated) {
        this.content = content;
        this.generated = generated;
    }
}
