package org.gaung.wiwokdetok.kapsulkeaslian.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordsMatchValidator.class)
public @interface PasswordsMatch {

    String message() default "Kata sandi dan konfirmasi tidak cocok.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
