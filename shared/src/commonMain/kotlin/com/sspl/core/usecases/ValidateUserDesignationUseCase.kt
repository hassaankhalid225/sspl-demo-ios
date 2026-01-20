package com.sspl.core.usecases

import com.sspl.ui.kyc.ValidationResult

class ValidateUserDesignationUseCase {
    operator fun invoke(designation: String?): ValidationResult {
//        if (designation.isNullOrEmpty()) {
//            return ValidationResult.Error("Designation cannot be empty")
//        }
        return ValidationResult.Success
    }
}