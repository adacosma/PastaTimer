package com.example.pastatimer

import com.example.pastatimer.SauceEntity
import com.example.pastatimer.UserEntity
import com.example.pastatimer.viewmodel.SauceViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class SauceViewModelTest {
    private val testSauces = listOf(
        SauceEntity(name = "Tomato", ingredients = "tomato, garlic, olive oil", imageResName = "tomato"),
        SauceEntity(name = "Cheesy", ingredients = "milk, parmesan, garlic", imageResName =  "cheese"),
        SauceEntity(name = "Carbonara", ingredients = "egg, cheese, bacon", imageResName =  "carbonara"),
        SauceEntity(name = "Bolognese", ingredients = "beef, tomato, onion", imageResName =  "bolognese"),
        SauceEntity(name = "Pesto", ingredients = "basil, olive oil", imageResName = "pesto")
    )

    @Test
    fun test_filter_by_allergens_only() {
        val user = UserEntity("ana", "123", allergens = "milk", isVegetarian = false)
        val vm = SauceViewModel(testSauces)
        vm.updateUser(user)

        val result = vm.filteredSauces.value.map { it.name }
        assertEquals(listOf("Tomato", "Bolognese", "Pesto"), result)
    }

    @Test
    fun test_filter_by_vegan_only() {
        val user = UserEntity("maria", "pass", allergens = "", isVegetarian = true)
        val vm = SauceViewModel(testSauces)
        vm.updateUser(user)

        val result = vm.filteredSauces.value.map { it.name }
        assertEquals(listOf("Tomato", "Cheesy", "Pesto"), result)
    }

    @Test
    fun test_filter_by_vegetarian_and_allergens() {
        val user = UserEntity("dan", "abc", allergens = "garlic", isVegetarian = true)
        val vm = SauceViewModel(testSauces)
        vm.updateUser(user)

        val result = vm.filteredSauces.value.map { it.name }
        assertEquals(listOf("Pesto"), result)
    }

    @Test
    fun test_all_sauces_filtered_out() {
        val user =
            UserEntity("ella", "xyz", allergens = "tomato, garlic, milk, beef, egg, basil", isVegetarian = false)
        val vm = SauceViewModel(testSauces)
        vm.updateUser(user)

        val result = vm.filteredSauces.value.map { it.name }
        assertEquals(emptyList<String>(), result)
    }
}