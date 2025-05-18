package com.example.pastatimer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val password: String,
    val isVegetarian: Boolean = false,
    val allergens: String = ""
)
