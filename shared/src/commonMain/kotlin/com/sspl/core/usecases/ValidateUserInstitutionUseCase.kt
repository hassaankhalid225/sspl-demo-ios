package com.sspl.core.usecases

import com.sspl.ui.kyc.ValidationResult

class ValidateUserInstitutionUseCase {
    operator fun invoke(institution: String?): ValidationResult {
        if (institution.isNullOrEmpty()) {
            return ValidationResult.Error("Institution cannot be empty")
        }
        return ValidationResult.Success
    }
}