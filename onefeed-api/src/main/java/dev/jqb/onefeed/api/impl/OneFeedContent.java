package dev.jqb.onefeed.api.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import dev.jqb.onefeed.api.feed.SourceInfo;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * The default implementation of {@link NormalizedContent}
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class OneFeedContent extends NormalizedContent {

    /**
     * The title of the content, using CommonMark-Flavored Markdown for any formatting.
     *
     * @see <a href="https://spec.commonmark.org/0.31.2/">CommonMark Spec</a>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;

    /**
     * The primary textual content, using CommonMark-Flavored Markdown for any formatting
     *
     * @see <a href="https://spec.commonmark.org/0.31.2/">CommonMark Spec</a>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String body;

    /**
     * Any attached media, such as links, videos, images, or files, in their desired order of
     * presentation or priority (high/first to low/last)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Media> media;

    /**
     * The quantity of whatever reaction type is primary on the source platform, the semantics of
     * which are discernable via interpretation of the content's source platform by the client
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int primaryReactionCount;

    /**
     * Constructs a piece of {@code OneFeedContent} attributed to a {@code source} and
     * created/published at the given time.
     *
     * @param source the origin of the content
     * @param nextPageCursor the cursor pointing to the next page of content after {@code this} (or
     *                       some equivalent means), if known, on the originating platform's API
     * @param published the time the {@code Content} was published on its {@code source}
     * @param body the primary textual content, using CommonMark-Flavored Markdown for any
     *             formatting
     */
    public OneFeedContent(SourceInfo source, @Nullable String nextPageCursor, Instant published,
        String body
    ) {
        super(source, nextPageCursor, published);
        this.body = body;
    }

    /**
     * Constructs a piece of {@code OneFeedContent}, containing just media. All other fields may be
     * set with setters.
     *
     * @param source the origin of the content
     * @param nextPageCursor the cursor pointing to the next page of content after {@code this} (or
     *                       some equivalent means), if known, on the originating platform's API
     * @param published the time the {@code Content} was published on its {@code source}
     * @param media any attached media, such as links, videos, images, or files, in their desired
     *              order of presentation or priority (high/first to low/last)
     */
    public OneFeedContent(SourceInfo source, @Nullable String nextPageCursor, Instant published,
        List<Media> media
    ) {
        super(source, nextPageCursor, published);
        this.media = media;
    }

    /**
     * Constructs a piece of {@code OneFeedContent}, containing both body text and media. All other
     * fields may be set with setters.
     *
     * @param source the origin of the content
     * @param nextPageCursor the cursor pointing to the next page of content after {@code this} (or
     *                       some equivalent means), if known, on the originating platform's API
     * @param published the time the {@code Content} was published on its {@code source}
     * @param body the primary textual content, using CommonMark-Flavored Markdown for any
     *             formatting
     * @param media any attached media, such as links, videos, images, or files, in their desired
     *              order of presentation or priority (high/first to low/last)
     */
    public OneFeedContent(SourceInfo source, @Nullable String nextPageCursor, Instant published,
        String body, List<Media> media
    ) {
        super(source, nextPageCursor, published);
        this.body = body;
        this.media = media;
    }
}
