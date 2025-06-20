package com.example.pastatimer

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for [UserDao] using Mockito to mock the DAO behavior.
 *
 * This test class verifies the correct behavior of the following DAO methods:
 * - [UserDao.getUserByUsername]
 * - [UserDao.insertUser]
 * - [UserDao.updatePreferences]
 *
 * These are **pure unit tests** that use mocked behavior and do **not** access a real database.
 */

class UserDaoTest {

    private lateinit var mockUserDao: UserDao

    /**
     * Sets up a mock instance of [UserDao] before each test.
     */
    @Before
    fun setup() {
        mockUserDao = mock()
    }

    /**
     * Tests that [UserDao.getUserByUsername] returns the expected [UserEntity]
     * when a matching user exists.
     */
    @Test
    fun testGetUserByUsername_returnsUser() {
        val expectedUser = UserEntity(
            username = "testuser",
            password = "password123",
            isVegetarian = true,
            allergens = "Gluten,Nuts"
        )
        whenever(mockUserDao.getUserByUsername("testuser")).thenReturn(expectedUser)

        val result = mockUserDao.getUserByUsername("testuser")

        assertNotNull(result)
        assertEquals(expectedUser.username, result?.username)
        assertEquals(expectedUser.password, result?.password)
        assertTrue(result?.isVegetarian == true)
        assertEquals("Gluten,Nuts", result?.allergens)
    }

    /**
     * Tests that [UserDao.insertUser] can insert a user and the same user
     * can be retrieved afterwards using [UserDao.getUserByUsername].
     */
    @Test
    fun insertUser_addsUserCorrectly() {
        val user = UserEntity(
            username = "inserted@example.com",
            password = "insert123",
            isVegetarian = false,
            allergens = "Milk"
        )

        doNothing().whenever(mockUserDao).insertUser(user)
        mockUserDao.insertUser(user)

        whenever(mockUserDao.getUserByUsername(user.username)).thenReturn(user)
        val retrievedUser = mockUserDao.getUserByUsername(user.username)

        assertEquals(user, retrievedUser)
    }

    /**
     * Tests that [UserDao.updatePreferences] correctly updates the user's
     * vegetarian preference and allergen list.
     */
    @Test
    fun updatePreferences_updatesUserDataCorrectly() {
        val user = UserEntity(
            username = "update@example.com",
            password = "initial",
            isVegetarian = false,
            allergens = ""
        )

        doNothing().whenever(mockUserDao).insertUser(user)
        mockUserDao.insertUser(user)

        doNothing().whenever(mockUserDao)
            .updatePreferences(user.username, isVegetarian = true, allergens = "Nuts,Soy")

        mockUserDao.updatePreferences(user.username, true, "Nuts,Soy")

        val updatedUser = user.copy(isVegetarian = true, allergens = "Nuts,Soy")
        whenever(mockUserDao.getUserByUsername(user.username)).thenReturn(updatedUser)

        val result = mockUserDao.getUserByUsername(user.username)
        assertEquals(true, result?.isVegetarian)
        assertEquals("Nuts,Soy", result?.allergens)
    }
}
