package com.example.pastatimer.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pastatimer.*
import com.example.pastatimer.repository.AppRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing sauce data and user preferences.
 *
 * Provides:
 * - Retrieval and management of favorite sauces
 * - Filtering of sauces based on user dietary preferences (vegetarian & allergens)
 * - Communication with the Room database via [AppRepository]
 */
class SauceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(
            db.sauceDao(),
            db.userDao(),
            db.pastaTypeDao(),
            db.userFavoriteSauceDao()
        )
    }

    var user by mutableStateOf<UserEntity?>(null)
        private set

    private val _favoriteSauces = mutableStateOf<List<SauceEntity>>(emptyList())
    val favoriteSauces: State<List<SauceEntity>> get() = _favoriteSauces

    private val _filteredSauces = mutableStateOf<List<SauceEntity>>(emptyList())
    val filteredSauces: State<List<SauceEntity>> get() = _filteredSauces

    fun loadFavorites(username: String) {
        viewModelScope.launch {
            _favoriteSauces.value = repository.getFavoritesForUser(username)
        }
    }

    fun toggleFavorite(username: String, sauce: SauceEntity) {
        viewModelScope.launch {
            val isFav = _favoriteSauces.value.any { it.id == sauce.id }
            if (isFav) {
                repository.removeFavorite(username, sauce.id)
            } else {
                repository.addFavorite(username, sauce.id)
            }
            loadFavorites(username)
        }
    }

    fun getAllSauces(): List<SauceEntity> {
        return repository.getAllSauces()
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

        _filteredSauces.value = repository.getAllSauces().filter { sauce ->
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
        val meat = listOf("beef", "pork", "chicken", "bacon", "meat", "ham", "sausage", "anchovies", "guanciale")
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
