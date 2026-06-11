package dev.jqb.onefeed.server.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A weight for a feed in an aggregation request
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FeedWeightConstraint
public class CustomAggregation {
    @Size(min=1, message="At least one feed must be specified")
    private List<@Valid WeightedFeed> weightedFeeds;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeightedFeed {
        /**
         * Corresponds to a {@link dev.jqb.onefeed.core.feed.FeedIdentifier}
         */
        @NotBlank
        private String feedId;

        /**
         * An optional weight for the feed
         */
        @Min(value=1, message="Weight must be at least 1")
        private Integer weight = 1;

        /**
         * Constructs a new {@code WeightedFeed} with the given feed ID and no weight.
         * @param feedId the ID of the feed
         * @see dev.jqb.onefeed.core.feed.FeedIdentifier#fromIdString(String)
         */
        public WeightedFeed(String feedId) {
            this.feedId = feedId;
        }
    }
}
