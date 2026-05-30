package dev.jqb.onefeed.api.aggregation;

import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.feed.Author;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A package of content and author data
 * @param <T> the type of {@link NormalizedContent} contained in this response
 * @param <U> the type of {@link Author} data contained in this response
 */
@Getter
@Setter
@ToString
public class AggregationPackage<T extends NormalizedContent, U extends Author> {

    /**
     * A map of feed identifier strings to their respective author data
     * @see dev.jqb.onefeed.api.feed.FeedIdentifier#toIdString()
     */
    private HashMap<String, U> authors;

    /**
     * A list of content in descending order of creation timestamp
     */
    private List<T> content;

    /**
     * Constructs a new {@code AggregationPackage} with the given {@code authors} and
     * {@code content}.
     *
     * @param authors a map of feed identifier strings to their respective author data, providing
     *                the author data for each feed repsented by the {@code content}
     * @param content a list of content in descending order of creation timestamp
     */
    public AggregationPackage(HashMap<String, U> authors, List<T> content) {
        this.authors = authors;
        this.content = content;
    }
}
