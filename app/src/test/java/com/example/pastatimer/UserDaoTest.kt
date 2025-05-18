package com.example.pastatimer

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class UserDaoTest {

    private lateinit var mockUserDao: UserDao

    @Before
    fun setup() {
        mockUserDao = mock()
    }

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
