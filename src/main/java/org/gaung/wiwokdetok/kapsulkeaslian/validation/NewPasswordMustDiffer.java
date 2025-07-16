package org.gaung.wiwokdetok.kapsulkeaslian.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NewPasswordMustDifferValidator.class)
public @interface NewPasswordMustDiffer {

    String message() default "Kata sandi baru tidak boleh sama dengan kata sandi saat ini.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

