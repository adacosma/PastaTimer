package com.example.pastatimer

import com.example.pastatimer.UserEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class RegistrationValidatorTest {

    private val existingUsers = listOf(
        UserEntity("ana", "1234", allergens = "", isVegetarian = false),
        UserEntity("bob", "abcd", allergens = "", isVegetarian = false)
    )

    @Test
    fun test_successful_registration() {
        val result = RegistrationValidator.validateRegistration(
            username = "newuser",
            password = "abc123",
            confirmPassword = "abc123",
            existingUsers = listOf()
        )
        assertEquals(RegistrationValidator.ValidationResult.Success, result)
    }

    @Test
    fun test_duplicate_username() {
        val result = RegistrationValidator.validateRegistration(
            username = "ana",
            password = "test",
            confirmPassword = "test",
            existingUsers = listOf(UserEntity("ana", "123", allergens = "", isVegetarian = false))
        )
        assert(result is RegistrationValidator.ValidationResult.Error)
        assertEquals("Username already exists", (result as RegistrationValidator.ValidationResult.Error).message)
    }

    @Test
    fun test_password_mismatch() {
        val result = RegistrationValidator.validateRegistration(
            username = "maria",
            password = "1234",
            confirmPassword = "4321",
            existingUsers = listOf()
        )
        assert(result is RegistrationValidator.ValidationResult.Error)
        assertEquals("Passwords do not match", (result as RegistrationValidator.ValidationResult.Error).message)
    }

    @Test
    fun test_empty_username() {
        val result = RegistrationValidator.validateRegistration(
            username = "",
            password = "abc",
            confirmPassword = "abc",
            existingUsers = listOf()
        )
        assert(result is RegistrationValidator.ValidationResult.Error)
        assertEquals("Username cannot be empty", (result as RegistrationValidator.ValidationResult.Error).message)
    }}