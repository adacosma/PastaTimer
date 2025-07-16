package com.example.pastatimer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SauceDao {

    @Insert
    suspend fun insertAll(sauces: List<SauceEntity>)

    @Query("SELECT * FROM sauces")
    suspend fun getAll(): List<SauceEntity>

}
