package com.sspl.core.usecases

import com.sspl.ui.kyc.ValidationResult

class ValidateUserRegistrationNumberUseCase {
    operator fun invoke(institution: String?): ValidationResult {
        if (institution.isNullOrEmpty()) {
            return ValidationResult.Error("Registration number cannot be empty")
        }
        return ValidationResult.Success
    }
}