package com.example.pastatimer

object RegistrationValidator {

    fun validateRegistration(
        username: String,
        password: String,
        confirmPassword: String,
        existingUsers: List<UserEntity>
    ): ValidationResult {
        if (username.isBlank()) return ValidationResult.Error("Username cannot be empty")
        if (existingUsers.any { user -> user.username == username }) return ValidationResult.Error("Username already exists")
        if (password != confirmPassword) return ValidationResult.Error("Passwords do not match")
        return ValidationResult.Success
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}