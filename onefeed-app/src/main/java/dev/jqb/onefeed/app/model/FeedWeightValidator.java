package dev.jqb.onefeed.app.model;

import dev.jqb.onefeed.app.model.CustomAggregation.FeedWeight;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that either all weights are null or none are null
 */
public class FeedWeightValidator implements ConstraintValidator<FeedWeightConstraint, CustomAggregation> {

    @Override
    public boolean isValid(CustomAggregation value, ConstraintValidatorContext context) {
        int numWeights = 0;

        for (FeedWeight fw : value.getFeedWeights()) {
            if (fw.getWeight() != null) {
                numWeights++;
            }
        }

        return numWeights == 0 || numWeights == value.getFeedWeights().size();
    }
}
