package com.example.pastatimer.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.pastatimer.SauceEntity
import com.example.pastatimer.UserEntity
import com.example.pastatimer.defaultSauceList
import com.example.pastatimer.SauceDao
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.pastatimer.AppDatabase

class SauceViewModel(application: Application) : AndroidViewModel(application) {
    private val sauceDao: SauceDao = AppDatabase.getDatabase(application).sauceDao()

    var user by mutableStateOf<UserEntity?>(null)
        private set

    private val _favoriteSauces = mutableStateOf<List<SauceEntity>>(emptyList())
    val favoriteSauces: State<List<SauceEntity>> get() = _favoriteSauces

    private val _filteredSauces = mutableStateOf<List<SauceEntity>>(emptyList())
    val filteredSauces: State<List<SauceEntity>> get() = _filteredSauces

    init {
        loadFavoriteSauces()
    }

    fun loadFavoriteSauces() {
        _favoriteSauces.value = sauceDao.getFavorites()
    }

    fun getAllSauces(): List<SauceEntity> {
        return sauceDao.getAll()
    }


    fun toggleFavorite(sauce: SauceEntity) {
        val newState = !sauce.isFavorite
        sauceDao.updateFavorite(sauce.id, newState)
        loadFavoriteSauces()
    }

    fun updateUser(userEntity: UserEntity) {
        user = userEntity
        filterSauces()
    }

    private fun filterSauces() {
        val currentUser = user ?: return

        val allergens = currentUser.allergens
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }

        val vegetarianOnly = currentUser.isVegetarian

        _filteredSauces.value = sauceDao.getAll().filter { sauce ->
            val ingredients = sauce.ingredients.lowercase()

            val containsAllergen = allergens.any { allergen ->
                allergenMap[allergen]?.any { ingredients.contains(it) } ?: ingredients.contains(allergen)
            }

            val hasMeat = containsMeat(ingredients)
            val isVegetarianOk = !vegetarianOnly || !hasMeat

            !containsAllergen && isVegetarianOk
        }
    }

    private fun containsMeat(ingredients: String): Boolean {
        val meat = listOf("beef", "pork", "chicken", "bacon", "meat", "ham", "sausage", "anchovies")
        return meat.any { ingredients.contains(it) }
    }

    private val allergenMap = mapOf(
        "milk" to listOf("milk", "cheese", "cream", "parmesan", "mozzarella", "cheddar", "pecorino", "gorgonzola", "butter", "dairy", "yogurt"),
        "eggs" to listOf("egg", "eggs", "egg yolk", "egg white"),
        "nuts" to listOf("almond", "hazelnut", "walnut", "cashew", "nut"),
        "fish" to listOf("fish", "anchovies", "salmon", "tuna"),
        "soy" to listOf("soy", "soy sauce", "tofu")
    )
}
