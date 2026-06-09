package dev.jqb.onefeed.api.aggregation;

import dev.jqb.onefeed.api.feed.FeedIdentifier;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Optional controls over what a final aggregation should contain
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AggregationOptions {

    /**
     * The weight of each feed in the aggregation, relative to each other. There is no max sum, but
     * all weights must be greater than 1
     */
    private Map<FeedIdentifier, Integer> feedWeights;

    /**
     * Creates a bundle of aggregation options.
     *
     * @param feedWeights the weight of each feed in the aggregation, relative to each other. There
     *                    is no max sum, but all weights must be greater than 1.
     */
    public AggregationOptions(Map<FeedIdentifier, Integer> feedWeights) {
        validateFeedWeights(feedWeights);
        this.feedWeights = feedWeights;
    }

    /**
     * Calculates the target amount of content for each feed based on the target sum and the feed
     * weights.
     * @param targetSum the desired sum of content across all feeds
     * @return a map of feed IDs to the target amount of content for that feed
     */
    public Map<FeedIdentifier, Integer> getTargetAmounts(int targetSum) {
        int weightSum = feedWeights.values().stream().mapToInt(Integer::intValue).sum();
        if (weightSum == 0) {
            weightSum = this.feedWeights.size();
        }

        Map<FeedIdentifier, Integer> targetAmounts = new HashMap<>();

        for (FeedIdentifier feedId : feedWeights.keySet()) {
            double proportionalAmount = (double) targetSum * feedWeights.get(feedId) / weightSum;
            targetAmounts.put(feedId, (int) Math.ceil(proportionalAmount));
        }

        return targetAmounts;
    }

    /**
     * Validates that all feed weights are greater than 1
     * @param feedWeights the weights of each feed
     */
    private static void validateFeedWeights(Map<FeedIdentifier, Integer> feedWeights) {
        for (int weight : feedWeights.values()) {
            if (weight < 1) {
                throw new IllegalArgumentException("All feed weights must be greater than 0");
            }
        }
    }
}
