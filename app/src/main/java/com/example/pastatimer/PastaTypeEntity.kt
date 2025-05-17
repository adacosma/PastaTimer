package com.example.pastatimer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pasta_types")
data class PastaTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val boilTime: Int,
    val imageResName: String,
    val flourType: String
)