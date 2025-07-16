package com.example.pastatimer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PastaTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pastas: List<PastaTypeEntity>)

    @Query("SELECT * FROM pasta_types")
    suspend fun getAll(): List<PastaTypeEntity>
}