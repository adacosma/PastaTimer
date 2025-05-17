package com.example.pastatimer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SauceDao {
    @Insert
    fun insertAll(sauces: List<SauceEntity>)

    @Query("SELECT * FROM sauces")
    fun getAll(): List<SauceEntity>
}
