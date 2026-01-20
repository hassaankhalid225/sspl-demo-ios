package com.sspl.core.usecases

import com.sspl.ui.kyc.ValidationResult

class ValidateUserNameUseCase {
    operator fun invoke(firstName: String?): ValidationResult {
        if (firstName.isNullOrEmpty()) {
            return ValidationResult.Error("First name cannot be empty")
        }
        if (firstName.any { it.isDigit() }) {
            return ValidationResult.Error("First name cannot contain numbers")
        }
        return ValidationResult.Success
    }
}