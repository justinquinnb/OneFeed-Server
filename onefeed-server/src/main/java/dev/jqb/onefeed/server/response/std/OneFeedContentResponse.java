package dev.jqb.onefeed.server.response.std;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.jqb.onefeed.core.feed.FeedAttribution;
import dev.jqb.onefeed.core.platform.ExternalRef;
import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * A piece of {@link dev.jqb.onefeed.core.content.OneFeedContent} sent to a client of OneFeed
 *
 * @param source the source feed the content is from
 * @param externalRef a means of accessing the resource on the source platform
 * @param nextPageCursor the cursor pointing to the next page of content after {@code this} (or
 *                       some equivalent means), if known, on the originating platform's API
 * @param published the time the {@code Content} was published on its {@code source}
 * @param authorIds the IDs of the authors of {@code this} content on the source platform
 * @param title the title of the content, using CommonMark-Flavored Markdown for any formatting
 * @param body the primary textual content, using CommonMark-Flavored Markdown for any formatting
 * @param attachments any attached attachments, such as links, videos, images, or files, in their
 *                    desired order of presentation or priority (high/first to low/last)
 * @param primaryReactionCount the quantity of whatever reaction type is primary on the
 *                             source platform, the semantics of which are discernable via
 *                             interpretation of the content's source platform by the client
 *
 * @see <a href="https://spec.commonmark.org/0.31.2/">CommonMark Spec</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OneFeedContentResponse(
    FeedAttributionResponse source,
    ExternalRefResponse externalRef,
    @Nullable String nextPageCursor,
    Instant published,
    List<String> authorIds,
    @Nullable String title,
    @Nullable String body,
    @Nullable List<OneFeedMediaResponse> attachments,
    int primaryReactionCount
) implements Streamable {

}
