package com.sspl.core.usecases

class PhoneNumberValidationUseCase {
    operator fun invoke(phoneNumber: String): Boolean {
        return phoneNumber.isNotEmpty() && phoneNumber.length in 10..15
    }
}