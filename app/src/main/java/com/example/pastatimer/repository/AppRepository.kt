package com.example.pastatimer.repository
import com.example.pastatimer.PastaTypeDao
import com.example.pastatimer.PastaTypeEntity
import com.example.pastatimer.SauceDao
import com.example.pastatimer.SauceEntity
import com.example.pastatimer.UserDao
import com.example.pastatimer.UserEntity
import com.example.pastatimer.UserFavoriteSauceDao
import com.example.pastatimer.model.UserFavoriteSauceEntity

/**
 * AppRepository is the bridge between the ViewModel and Room database DAOs.
 * It implements the IAppRepository interface to decouple business logic from data access.
 *
 * This is a key component of the MVVM architecture pattern.
 */
class AppRepository(
    private val userDao: UserDao,
    private val pastaTypeDao: PastaTypeDao,
    private val sauceDao: SauceDao,
    private val userFavoriteSauceDao: UserFavoriteSauceDao
) : IAppRepository {  // implementează interfața aici!

    override suspend fun getAllPastaTypes(): List<PastaTypeEntity> = pastaTypeDao.getAll()
    override suspend fun getAllSauces(): List<SauceEntity> = sauceDao.getAll()
    override suspend fun getFavoritesForUser(username: String): List<SauceEntity> = userFavoriteSauceDao.getFavoritesForUser(username)
    override suspend fun getUserByUsername(username: String): UserEntity? = userDao.getUserByUsername(username)
    override suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    override suspend fun insertPastaTypes(types: List<PastaTypeEntity>) = pastaTypeDao.insertAll(types)
    override suspend fun insertSauces(sauces: List<SauceEntity>) = sauceDao.insertAll(sauces)
    override suspend fun addFavorite(username: String, sauceId: Int) = userFavoriteSauceDao.addFavorite(
        UserFavoriteSauceEntity(username, sauceId)
    )
    override suspend fun removeFavorite(username: String, sauceId: Int) = userFavoriteSauceDao.removeFavorite(username, sauceId)
    override suspend fun updateUserPreferences(username: String, isVegetarian: Boolean, allergens: String) =
        userDao.updatePreferences(username, isVegetarian, allergens)
}
