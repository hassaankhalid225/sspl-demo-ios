package com.sspl.core.usecases

import com.sspl.ui.kyc.ValidationResult

class ValidateUserPmdcNoUseCase {
    operator fun invoke(pmdcNo: String?): ValidationResult {
//        if (pmdcNo.isNullOrEmpty()) {
//            return ValidationResult.Error("PMDC number cannot be empty")
//        }
        return ValidationResult.Success
    }
}