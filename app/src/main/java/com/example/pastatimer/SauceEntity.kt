
package com.example.pastatimer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sauces")
data class SauceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val ingredients: String,
    val imageResName: String
)
