package dev.jqb.onefeed.api.feed;

import dev.jqb.onefeed.api.author.PlatformAuthor;
import dev.jqb.onefeed.api.content.PlatformContent;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * A collection of changes to a feed's content and/or profile
 */
@Getter
@Setter
@Builder
public class FeedUpdate<C extends PlatformContent, A extends PlatformAuthor> {

    /**
     * Any new content that was added to the feed
     */
    @Builder.Default
    private List<C> newContent = List.of();

    /**
     * Any content that was updated in the feed
     */
    @Builder.Default
    private List<C> updatedContent = List.of();

    /**
     * Any content that was removed from the feed
     */
    @Builder.Default
    private List<C> removedContent = List.of();

    /**
     * The updated profile of the feed's author
     */
    private A updatedAuthor;
}
