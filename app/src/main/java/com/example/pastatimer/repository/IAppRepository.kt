package com.example.pastatimer.repository

import com.example.pastatimer.PastaTypeEntity
import com.example.pastatimer.SauceEntity
import com.example.pastatimer.UserEntity

interface IAppRepository {
    suspend fun getAllPastaTypes(): List<PastaTypeEntity>
    suspend fun getAllSauces(): List<SauceEntity>
    suspend fun getFavoritesForUser(username: String): List<SauceEntity>
    suspend fun getUserByUsername(username: String): UserEntity?
    suspend fun insertUser(user: UserEntity)
    suspend fun insertPastaTypes(types: List<PastaTypeEntity>)
    suspend fun insertSauces(sauces: List<SauceEntity>)
    suspend fun addFavorite(username: String, sauceId: Int)
    suspend fun removeFavorite(username: String, sauceId: Int)
}