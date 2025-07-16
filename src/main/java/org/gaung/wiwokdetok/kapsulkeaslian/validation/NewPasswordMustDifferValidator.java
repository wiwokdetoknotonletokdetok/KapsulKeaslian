package org.gaung.wiwokdetok.kapsulkeaslian.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdatePasswordRequest;

public class NewPasswordMustDifferValidator implements ConstraintValidator<NewPasswordMustDiffer, UpdatePasswordRequest> {

    @Override
    public boolean isValid(UpdatePasswordRequest request, ConstraintValidatorContext context) {
        return !request.getNewPassword().equals(request.getCurrentPassword());
    }
}

