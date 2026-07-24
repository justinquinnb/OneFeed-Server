package dev.jqb.onefeed.server.response.std;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;

/**
 * A piece of {@link dev.jqb.onefeed.core.content.OneFeedMedia} sent to a client of OneFeed
 *
 * @param href the link to the attachment on its host platform
 * @param thumbnailSrc sets the source of the attachment's thumbnail
 * @param title the title or name of the attachment (such as the title of a link)
 * @param caption a caption for the attachment
 * @param mimeType the type of media being represented, adhering to
 *                 <a href="https://datatracker.ietf.org/doc/html/rfc6838">RFC 6838</a> as an
 *                 official entry in the <a href="https://www.iana.org/assignments/media-types/media-types.xhtml">IANA Media Types registry</a>.
 * @param src a URL to the media resource itself, for direct embedding
 * @param altText alt text for the piece of media
 v*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OneFeedMediaResponse(
    String href,
    @Nullable String thumbnailSrc,
    @Nullable String title,
    @Nullable String caption,
    @Nullable String mimeType,
    @Nullable String src,
    @Nullable String altText
) {

}
