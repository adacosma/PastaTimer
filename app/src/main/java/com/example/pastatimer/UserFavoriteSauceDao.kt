package com.example.pastatimer
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pastatimer.model.UserFavoriteSauceEntity


@Dao
interface UserFavoriteSauceDao {

    @Insert
    fun addFavorite(fav: UserFavoriteSauceEntity)

    @Query("DELETE FROM user_favorite_sauces WHERE username = :username AND sauceId = :sauceId")
    fun removeFavorite(username: String, sauceId: Int)

    @Query("""
        SELECT * FROM sauces WHERE id IN (
            SELECT sauceId FROM user_favorite_sauces WHERE username = :username
        )
    """)
    fun getFavoritesForUser(username: String): List<SauceEntity>
}
