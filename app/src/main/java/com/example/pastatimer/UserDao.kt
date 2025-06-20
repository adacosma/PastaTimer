package com.example.pastatimer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Data Access Object (DAO) interface for accessing and modifying user data in the Room database.
 *
 * Provides methods to insert users, query users by username, retrieve all users,
 * and update dietary preferences (vegetarian flag and allergens).
 */

@Dao
interface UserDao {

    /**
     * Inserts a new [UserEntity] into the users table.
     *
     * @param user The user entity to be inserted.
     */
    @Insert
    fun insertUser(user: UserEntity)

    /**
     * Retrieves a user by their username.
     *
     * @param username The username to search for.
     * @return The [UserEntity] if found, or null otherwise.
     */
    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): UserEntity?

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all [UserEntity] objects stored in the users table.
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): List<UserEntity>

    /**
     * Updates the dietary preferences (vegetarian status and allergens) for a given user.
     *
     * @param username The username of the user to update.
     * @param isVegetarian True if the user prefers vegetarian recipes.
     * @param allergens A comma-separated list of allergens to avoid.
     */
    @Query("UPDATE users SET isVegetarian = :isVegetarian, allergens = :allergens WHERE username = :username")
    fun updatePreferences(username: String, isVegetarian: Boolean, allergens: String)
}
