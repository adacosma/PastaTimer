package com.example.pastatimer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<UserEntity>

    @Query("UPDATE users SET isVegan = :isVegan, allergens = :allergens WHERE username = :username")
    fun updatePreferences(username: String, isVegan: Boolean, allergens: String)
}
