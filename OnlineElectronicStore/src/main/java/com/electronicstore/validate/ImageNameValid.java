package com.electronicstore.validate;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ImageNameValidator.class)
public @interface ImageNameValid {
    //default error message
    java.lang.String message() default "Invalid imagename...";

    //represent group of constraints
    java.lang.Class<?>[] groups() default {};

    //additional information about annotations
    java.lang.Class<? extends jakarta.validation.Payload>[] payload() default {};

}
