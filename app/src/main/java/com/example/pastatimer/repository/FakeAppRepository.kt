package com.example.pastatimer.repository
import com.example.pastatimer.PastaTypeDao
import com.example.pastatimer.PastaTypeEntity
import com.example.pastatimer.SauceDao
import com.example.pastatimer.SauceEntity
import com.example.pastatimer.UserDao
import com.example.pastatimer.UserEntity
import com.example.pastatimer.UserFavoriteSauceDao


class FakeAppRepository : IAppRepository {

    private val pastaTypes = mutableListOf<PastaTypeEntity>()
    private val sauces = mutableListOf<SauceEntity>()
    private val users = mutableListOf<UserEntity>()
    private val favorites = mutableListOf<Pair<String, Int>>()

    override suspend fun getAllPastaTypes(): List<PastaTypeEntity> = pastaTypes
    override suspend fun getAllSauces(): List<SauceEntity> = sauces
    override suspend fun getFavoritesForUser(username: String): List<SauceEntity> {
        val favoriteIds = favorites.filter { it.first == username }.map { it.second }
        return sauces.filter { it.id in favoriteIds }
    }
    override suspend fun getUserByUsername(username: String): UserEntity? = users.find { it.username == username }
    override suspend fun insertUser(user: UserEntity) {
        users.removeAll { it.username == user.username }
        users.add(user)
    }
    override suspend fun insertPastaTypes(types: List<PastaTypeEntity>) {
        pastaTypes.clear()
        pastaTypes.addAll(types)
    }
    override suspend fun insertSauces(sauces: List<SauceEntity>) {
        this.sauces.clear()
        this.sauces.addAll(sauces)
    }
    override suspend fun addFavorite(username: String, sauceId: Int) {
        if (!favorites.contains(username to sauceId)) favorites.add(username to sauceId)
    }
    override suspend fun removeFavorite(username: String, sauceId: Int) {
        favorites.remove(username to sauceId)
    }
}
