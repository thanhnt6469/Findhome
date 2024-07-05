package com.app.findhome.data.model

object Validator {
    fun validateFirstName(fName: String): ValidationResult {
        val namePattern = "^[a-zA-Z]+$"
        return ValidationResult(
            (fName.isNotEmpty() && fName.length >= 4 && fName.matches(namePattern.toRegex()))
        )
    }

    fun validateLastName(lName: String): ValidationResult {
        val namePattern = "^[a-zA-Z]+$"
        return ValidationResult(
            (lName.isNotEmpty() && lName.length >= 4 && lName.matches(namePattern.toRegex()))
        )
    }

    fun validateEmail(email: String): ValidationResult {
        val emailPattern = "^[A-Za-z0-9+_.-]+@gmail\\.com$"
        return ValidationResult(
            (email.isNotEmpty() && email.matches(emailPattern.toRegex()))
        )
    }

    fun validatePassword(password: String): ValidationResult {
        val passwordPattern = "^[A-Za-z0-9!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{6,}$"
        return ValidationResult(
            (password.isNotEmpty() && password.matches(passwordPattern.toRegex()))
        )
    }
}

data class ValidationResult(
    val status :Boolean = false
)