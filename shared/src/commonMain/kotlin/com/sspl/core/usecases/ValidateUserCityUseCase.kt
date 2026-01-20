package com.sspl.core.usecases

import com.sspl.ui.kyc.ValidationResult

class ValidateUserCityUseCase {
    operator fun invoke(city: String?): ValidationResult {
        if (city.isNullOrEmpty()) {
            return ValidationResult.Error("City cannot be empty")
        }
        return ValidationResult.Success
    }
}