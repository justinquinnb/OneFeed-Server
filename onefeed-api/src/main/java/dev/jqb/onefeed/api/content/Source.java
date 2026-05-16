package dev.jqb.onefeed.api.content;

import dev.jqb.onefeed.api.feed.FeedInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Information about a unit of {@link Content}'s source
 */
@Getter
@Setter
@ToString
public class Source {

    /**
     * The feed that the content originated from
     */
    private FeedInfo feed;

    /**
     * The URL of the content resource on its feed's platform
     */
    private String url;

    /**
     * Creates a new {@code Source} instance, indicating the source of a piece of
     * {@link Content}.
     *
     * @param feed the feed that the content originated from
     * @param url the URL for the specific piece of content
     */
    public Source(FeedInfo feed, String url) {
        this.feed = feed;
        this.url = url;
    }
}
