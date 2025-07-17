package com.example.pastatimer

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.pastatimer.model.UserFavoriteSauceEntity


/**
 * Room database definition for the PastaTimer application.
 *
 * This class serves as the main access point to the local SQLite database,
 * and provides abstract methods to access each DAO (Data Access Object).
 *
 * Follows the Singleton design pattern to ensure a single shared instance
 * of the database is used across the app.
 *
 * @Database defines the entities (tables) and the version of the database.
 * If the schema changes, you must increase the version number.
 */
@Database(entities = [PastaTypeEntity::class, SauceEntity::class,  UserEntity::class, UserFavoriteSauceEntity::class], version = 10)
abstract class AppDatabase : RoomDatabase() {
    // Abstract accessors for each DAO interface (for dependency injection)
    abstract fun pastaTypeDao(): PastaTypeDao
    abstract fun sauceDao(): SauceDao
    abstract fun userDao(): UserDao
    abstract fun userFavoriteSauceDao(): UserFavoriteSauceDao

    companion object {
        // Singleton instance (volatile for thread safety)
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns a single shared instance of the database for the application.
         * Builds the database using Room’s builder and enables destructive migrations
         * (useful in development to reset the DB on schema change).
         *
         * @param context Application context
         * @return Instance of AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pasta_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}