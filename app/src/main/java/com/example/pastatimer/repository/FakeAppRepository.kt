package com.example.pastatimer.viewmodel

import com.example.pastatimer.*
import com.example.pastatimer.repository.AppRepository

class FakeAppRepository : AppRepository(
    object : UserDao {
        private val users = mutableListOf<UserEntity>()

        override suspend fun insert(user: UserEntity) {
            users.add(user)
        }

        override suspend fun getUserByUsername(username: String): UserEntity? {
            return users.find { it.username == username }
        }
    },
    object : SauceDao {
        private val sauces = mutableListOf<SauceEntity>()

        override suspend fun insertAll(sauceList: List<SauceEntity>) {
            sauces.clear()
            sauces.addAll(sauceList)
        }

        override suspend fun getAll(): List<SauceEntity> = sauces
    },
    object : PastaTypeDao {
        private val pastaTypes = mutableListOf<PastaTypeEntity>()

        override suspend fun insertAll(pastaList: List<PastaTypeEntity>) {
            pastaTypes.clear()
            pastaTypes.addAll(pastaList)
        }

        override suspend fun getAll(): List<PastaTypeEntity> = pastaTypes
    },
    object : FavoriteSauceDao {
        private val favorites = mutableListOf<UserFavoriteSauceEntity>()

        override suspend fun insert(fav: UserFavoriteSauceEntity) {
            favorites.add(fav)
        }

        override suspend fun delete(username: String, sauceId: Int) {
            favorites.removeAll { it.username == username && it.sauceId == sauceId }
        }

        override suspend fun getFavoritesForUser(username: String): List<SauceEntity> {
            return favorites
                .filter { it.username == username }
                .mapNotNull { fav ->
                    dummySauceList.find { it.id == fav.sauceId }
                }
        }

        private val dummySauceList: List<SauceEntity>
            get() = listOf( // doar câteva pentru test
                SauceEntity(1, "Tomato", "tomato, salt, oil"),
                SauceEntity(2, "Pesto", "basil, oil, nuts")
            )
    }
)
