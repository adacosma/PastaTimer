package com.example.pastatimer

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.pastatimer.model.UserFavoriteSauceEntity


/**
 * Room database class for the PastaTimer app.
 *
 * Provides DAOs for accessing PastaType, Sauce, and User entities.
 * Uses a singleton pattern to ensure a single instance of the database
 * is used throughout the app.
 *
 * Database version: 9 (with destructive migrations allowed).
 */
@Database(entities = [PastaTypeEntity::class, SauceEntity::class,  UserEntity::class, UserFavoriteSauceEntity::class], version = 10)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pastaTypeDao(): PastaTypeDao
    abstract fun sauceDao(): SauceDao
    abstract fun userDao(): UserDao
    abstract fun userFavoriteSauceDao(): UserFavoriteSauceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pasta_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}