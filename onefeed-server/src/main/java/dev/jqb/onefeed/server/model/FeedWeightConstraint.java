package dev.jqb.onefeed.server.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FeedWeightValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FeedWeightConstraint {
    String message() default "All feeds must either have weights or have no weights";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
