package dev.jqb.onefeed.api.feed;

import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.impl.Profile;
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
public class FeedUpdate<T extends PlatformContent> {

    /**
     * Any new content that was added to the feed
     */
    @Builder.Default
    private List<T> newContent = List.of();

    /**
     * Any content that was updated in the feed
     */
    @Builder.Default
    private List<T> updatedContent = List.of();

    /**
     * Any content that was removed from the feed
     */
    @Builder.Default
    private List<T> removedContent = List.of();

    /**
     * The updated profile of the feed's author
     */
    private Profile updatedProfile;
}
