package dev.jqb.onefeed.api.aggregation;

import java.util.HashMap;
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
     * Whether to dedupe the content in the aggregation
     */
    private boolean dedupe;

    /**
     * The weight of each feed in the aggregation, relative to each other. There is no max sum, but
     * all weights must be greater than 1
     */
    private HashMap<String, Integer> feedWeights;

    /**
     * Creates a bundle of aggregation options.
     *
     * @param dedupe whether to dedupe the content in the aggregation
     * @param feedWeights the weight of each feed in the aggregation, relative to each other. There
     *                    is no max sum, but all weights must be greater than 1.
     */
    public AggregationOptions(boolean dedupe, HashMap<String, Integer> feedWeights) {
        validateFeedWeights(feedWeights);
        this.dedupe = dedupe;
        this.feedWeights = feedWeights;
    }

    /**
     * Calculates the target amount of content for each feed based on the target sum and the feed
     * weights.
     * @param targetSum the desired sum of content across all feeds
     * @return a map of feed IDs to the target amount of content for that feed
     */
    public HashMap<String, Integer> getTargetAmounts(int targetSum) {
        int weightSum = feedWeights.values().stream().mapToInt(Integer::intValue).sum();
        if (weightSum == 0) {
            weightSum = this.feedWeights.size();
        }

        HashMap<String, Integer> targetAmounts = new HashMap<>();

        for (String feedId : feedWeights.keySet()) {
            double proportionalAmount = (double) targetSum * feedWeights.get(feedId) / weightSum;
            targetAmounts.put(feedId, (int) Math.ceil(proportionalAmount));
        }

        return targetAmounts;
    }

    /**
     * Validates that all feed weights are greater than 1
     * @param feedWeights the weights of each feed
     */
    private static void validateFeedWeights(HashMap<String, Integer> feedWeights) {
        for (int weight : feedWeights.values()) {
            if (weight < 1) {
                throw new IllegalArgumentException("All feed weights must be greater than 0");
            }
        }
    }
}
