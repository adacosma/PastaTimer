package com.example.pastatimer.model

import androidx.room.Entity

@Entity(
    tableName = "user_favorite_sauces",
    primaryKeys = ["username", "sauceId"]
)
data class UserFavoriteSauceEntity(
    val username: String,
    val sauceId: Int
)

