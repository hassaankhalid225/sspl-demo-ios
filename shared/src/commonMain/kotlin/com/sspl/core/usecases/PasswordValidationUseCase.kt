package com.sspl.core.usecases

class PasswordValidationUseCase {
    operator fun invoke(password: String): Boolean {
        return password.length >= 8
    }
}