package com.example.pastatimer.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.pastatimer.SauceEntity
import com.example.pastatimer.UserEntity
import com.example.pastatimer.defaultSauceList

class SauceViewModel(
    private val allSauces: List<SauceEntity> = defaultSauceList
) : ViewModel() {
    var user by mutableStateOf<UserEntity?>(null)
        private set


    private val _filteredSauces = mutableStateOf<List<SauceEntity>>(emptyList())
    val filteredSauces: State<List<SauceEntity>> get() = _filteredSauces

    private val allergenMap = mapOf(
        "milk" to listOf("milk", "cheese", "cream", "parmesan", "mozzarella", "cheddar", "pecorino", "gorgonzola", "butter", "dairy", "yogurt"),
        "eggs" to listOf("egg", "eggs", "egg yolk", "egg white"),
        "nuts" to listOf("almond", "hazelnut", "walnut", "cashew", "nut"),
        "fish" to listOf("fish", "anchovies", "salmon", "tuna"),
        "soy" to listOf("soy", "soy sauce", "tofu")
    )

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

        _filteredSauces.value = allSauces.filter { sauce ->
            val ingredients = sauce.ingredients.lowercase()

            var containsAllergen = false
            for (allergen in allergens) {
                val synonyms = allergenMap[allergen] ?: listOf(allergen)
                for (term in synonyms) {
                    if (ingredients.contains(term)) {
                        containsAllergen = true
                        break
                    }
                }
                if (containsAllergen) break
            }

            val noAllergen = !containsAllergen

            val hasMeat = containsMeat(ingredients)
            val isVegetarianOk = !vegetarianOnly || !hasMeat

            return@filter noAllergen && isVegetarianOk
        }

    }

    private fun containsMeat(ingredients: String): Boolean {
        val meat = listOf("beef", "pork", "chicken", "bacon", "meat", "ham", "sausage", "anchovies")
        for (product in meat) {
            if (ingredients.contains(product)) {
                return true
            }
        }
        return false
    }



}
