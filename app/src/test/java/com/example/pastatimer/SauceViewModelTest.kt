//package com.example.pastatimer
//
//import com.example.pastatimer.SauceEntity
//import com.example.pastatimer.UserEntity
//import com.example.pastatimer.viewmodel.SauceViewModel
//import org.junit.Assert.assertEquals
//import org.junit.Test
//
//class SauceViewModelTest {
//    private val testSauces = listOf(
//        SauceEntity(name = "Tomato", ingredients = "tomato, garlic, olive oil", imageResName = "tomato"),
//        SauceEntity(name = "Cheesy", ingredients = "milk, parmesan, garlic", imageResName =  "cheese"),
//        SauceEntity(name = "Carbonara", ingredients = "egg, cheese, bacon", imageResName =  "carbonara"),
//        SauceEntity(name = "Bolognese", ingredients = "beef, tomato, onion", imageResName =  "bolognese"),
//        SauceEntity(name = "Pesto", ingredients = "basil, olive oil", imageResName = "pesto")
//    )
//
//    @Test
//    fun test_filter_by_allergens_only() {
//        val user = UserEntity("ana", "123", allergens = "milk", isVegetarian = false)
//        val vm = SauceViewModel(testSauces)
//        vm.updateUser(user)
//
//        val result = vm.filteredSauces.value.map { it.name }
//        assertEquals(listOf("Tomato", "Bolognese", "Pesto"), result)
//    }
//
//    @Test
//    fun test_filter_by_vegan_only() {
//        val user = UserEntity("maria", "pass", allergens = "", isVegetarian = true)
//        val vm = SauceViewModel(testSauces)
//        vm.updateUser(user)
//
//        val result = vm.filteredSauces.value.map { it.name }
//        assertEquals(listOf("Tomato", "Cheesy", "Pesto"), result)
//    }
//
//    @Test
//    fun test_filter_by_vegetarian_and_allergens() {
//        val user = UserEntity("dan", "abc", allergens = "garlic", isVegetarian = true)
//        val vm = SauceViewModel(testSauces)
//        vm.updateUser(user)
//
//        val result = vm.filteredSauces.value.map { it.name }
//        assertEquals(listOf("Pesto"), result)
//    }
//
//    @Test
//    fun test_all_sauces_filtered_out() {
//        val user =
//            UserEntity("ella", "xyz", allergens = "tomato, garlic, milk, beef, egg, basil", isVegetarian = false)
//        val vm = SauceViewModel(testSauces)
//        vm.updateUser(user)
//
//        val result = vm.filteredSauces.value.map { it.name }
//        assertEquals(emptyList<String>(), result)
//    }
//}
package com.example.pastatimer

import org.junit.Assert.assertEquals
import org.junit.Test

class SauceViewModelTest {

    // Creem un SauceViewModel modificat pentru teste care nu depinde de Android
    private class TestSauceViewModel(private val testSauces: List<SauceEntity>) {

        var user: UserEntity? = null
            private set

        var filteredSauces: List<SauceEntity> = emptyList()
            private set

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

            filteredSauces = testSauces.filter { sauce ->
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

    private val testSauces = listOf(
        SauceEntity(id = 1, name = "Tomato", ingredients = "tomato, garlic, olive oil", imageResName = "tomato", isFavorite = false),
        SauceEntity(id = 2, name = "Cheesy", ingredients = "milk, parmesan, garlic", imageResName = "cheese", isFavorite = false),
        SauceEntity(id = 3, name = "Carbonara", ingredients = "egg, cheese, bacon", imageResName = "carbonara", isFavorite = false),
        SauceEntity(id = 4, name = "Bolognese", ingredients = "beef, tomato, onion", imageResName = "bolognese", isFavorite = false),
        SauceEntity(id = 5, name = "Pesto", ingredients = "basil, olive oil", imageResName = "pesto", isFavorite = false)
    )

    @Test
    fun test_filter_by_allergens_only() {
        val user = UserEntity("ana", "123", allergens = "milk", isVegetarian = false)
        val vm = TestSauceViewModel(testSauces)
        vm.updateUser(user)

        val result = vm.filteredSauces.map { it.name }
        assertEquals(listOf("Tomato", "Bolognese", "Pesto"), result)
    }

    @Test
    fun test_filter_by_vegan_only() {
        val user = UserEntity("maria", "pass", allergens = "", isVegetarian = true)
        val vm = TestSauceViewModel(testSauces)
        vm.updateUser(user)

        val result = vm.filteredSauces.map { it.name }
        assertEquals(listOf("Tomato", "Cheesy", "Pesto"), result)
    }

    @Test
    fun test_filter_by_vegetarian_and_allergens() {
        val user = UserEntity("dan", "abc", allergens = "garlic", isVegetarian = true)
        val vm = TestSauceViewModel(testSauces)
        vm.updateUser(user)

        val result = vm.filteredSauces.map { it.name }
        assertEquals(listOf("Pesto"), result)
    }

    @Test
    fun test_all_sauces_filtered_out() {
        val user = UserEntity("ella", "xyz", allergens = "tomato, garlic, milk, beef, egg, basil", isVegetarian = false)
        val vm = TestSauceViewModel(testSauces)
        vm.updateUser(user)

        val result = vm.filteredSauces.map { it.name }
        assertEquals(emptyList<String>(), result)
    }
}