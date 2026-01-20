package com.sspl.core.usecases

import com.sspl.ui.kyc.ValidationResult

class ValidateUserCountryUseCase {
    operator fun invoke(country: String?): ValidationResult {
        if (country.isNullOrEmpty()) {
            return ValidationResult.Error("Country cannot be empty")
        }
        return ValidationResult.Success
    }
}