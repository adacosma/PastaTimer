package com.example.pastatimer

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a user in the Room database.
 *
 * This entity corresponds to the `users` table and stores:
 * - authentication data (username, password),
 * - dietary preferences (vegetarian flag, list of allergens).
 *
 * @property username Unique identifier for the user (used as primary key).
 * @property password The password associated with the user account.
 * @property isVegetarian Whether the user prefers vegetarian recipes (default: false).
 * @property allergens A comma-separated string of allergens the user wants to avoid (default: empty).
 */

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val password: String,
    val isVegetarian: Boolean = false,
    val allergens: String = ""
)
