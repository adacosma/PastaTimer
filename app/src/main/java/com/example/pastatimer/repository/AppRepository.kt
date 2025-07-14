package com.example.pastatimer.repository

import com.example.pastatimer.*
import com.example.pastatimer.model.UserFavoriteSauceEntity

class AppRepository(
    private val sauceDao: SauceDao,
    private val userDao: UserDao,
    private val pastaTypeDao: PastaTypeDao,
    private val userFavoriteSauceDao: UserFavoriteSauceDao
) {
    fun getAllSauces(): List<SauceEntity> = sauceDao.getAll()

    fun getAllPastaTypes(): List<PastaTypeEntity> = pastaTypeDao.getAll()

    suspend fun getUserByUsername(username: String): UserEntity? = userDao.getUserByUsername(username)

    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)


    fun getFavoritesForUser(username: String): List<SauceEntity> =
        userFavoriteSauceDao.getFavoritesForUser(username)

    fun addFavorite(username: String, sauceId: Int) {
        userFavoriteSauceDao.addFavorite(UserFavoriteSauceEntity(username, sauceId))
    }

    fun removeFavorite(username: String, sauceId: Int) {
        userFavoriteSauceDao.removeFavorite(username, sauceId)
    }

    fun insertSauces(sauces: List<SauceEntity>) = sauceDao.insertAll(sauces)

    fun insertPastaTypes(pastaTypes: List<PastaTypeEntity>) = pastaTypeDao.insertAll(pastaTypes)
}
