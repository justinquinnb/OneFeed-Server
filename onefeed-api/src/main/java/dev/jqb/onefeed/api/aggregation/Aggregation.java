package dev.jqb.onefeed.api.aggregation;

import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.author.Author;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * A complete aggregation of content and (optionally) authors
 */
@Getter
@Setter
@ToString
public class Aggregation {

    /**
     * The authors of the aggregated content, indexed by the ID of the feed they came from
     */
    private Map<FeedIdentifier, Author> authors;

    /**
     * The aggregated content, in descending chronological order
     */
    private List<? extends NormalizedContent> content;

    /**
     * The cursor that can be used to retrieve the next batch of aggregated content
     */
    private String aggregateCursor;

    /**
     * Constructs a new {@code Aggregation} with the given content and aggregate cursor to the next
     * batch/page.
     *
     * @param authors the authors of the aggregated content, indexed by the ID of the feed they came
     *               from
     * @param content the aggregated content
     * @param aggregateCursor the cursor to the next batch/page of aggregated content
     */
    public Aggregation(
        @Nullable Map<FeedIdentifier, Author> authors,
        List<? extends NormalizedContent> content,
        @Nullable String aggregateCursor
    ) {
        this.authors = authors;

        ArrayList<? extends NormalizedContent> sortedContent = new ArrayList<>(content);
        sortedContent.sort(NormalizedContent::compareTo);
        this.content = sortedContent;

        this.aggregateCursor = aggregateCursor;
    }
}
