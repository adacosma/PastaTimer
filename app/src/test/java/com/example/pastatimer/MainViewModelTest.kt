//package com.example.pastatimer
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.lifecycle.Observer
//import com.example.pastatimer.model.UserFavoriteSauceEntity
//import com.example.pastatimer.repository.IAppRepository
//import com.example.pastatimer.viewmodel.MainViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.test.*
//import org.junit.*
//import org.junit.Assert.*
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import kotlinx.coroutines.test.runTest
//import org.junit.Test
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import org.mockito.kotlin.argumentCaptor
//import org.mockito.kotlin.atLeast
//import org.mockito.kotlin.atLeastOnce
//import org.mockito.kotlin.times
//
////@OptIn(ExperimentalCoroutinesApi::class)
//@RunWith(RobolectricTestRunner::class)
//class MainViewModelTest {
//
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//    private val testDispatcher = StandardTestDispatcher()
//
//    private lateinit var viewModel: MainViewModel
//
//    // Fake repository simplificat pentru teste
//    class FakeAppRepository : IAppRepository {
//        private val sauces = mutableListOf<SauceEntity>()
//        private val favorites = mutableListOf<UserFavoriteSauceEntity>()
//        private val users = mutableListOf<UserEntity>()
//
//        override suspend fun getAllPastaTypes(): List<PastaTypeEntity> = emptyList()
//
//        override suspend fun getAllSauces(): List<SauceEntity> = sauces.toList()
//
//        override suspend fun getFavoritesForUser(username: String): List<SauceEntity> {
//            val favIds = favorites.filter { it.username == username }.map { it.sauceId }
//            return sauces.filter { it.id in favIds }
//        }
//
//        override suspend fun getUserByUsername(username: String): UserEntity? =
//            users.find { it.username == username }
//
//        override suspend fun insertUser(user: UserEntity) {
//            users.removeAll { it.username == user.username }
//            users.add(user)
//        }
//
//        override suspend fun insertPastaTypes(types: List<PastaTypeEntity>) {}
//
//        override suspend fun insertSauces(saucesList: List<SauceEntity>) {
//            sauces.clear()
//            sauces.addAll(saucesList)
//        }
//
//        override suspend fun addFavorite(username: String, sauceId: Int) {
//            if (!favorites.any { it.username == username && it.sauceId == sauceId }) {
//                favorites.add(UserFavoriteSauceEntity(username, sauceId))
//            }
//        }
//
//        override suspend fun removeFavorite(username: String, sauceId: Int) {
//            favorites.removeAll { it.username == username && it.sauceId == sauceId }
//        }
//    }
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//
//        val fakeRepo = FakeAppRepository()
//        runTest {
//            fakeRepo.insertSauces(
//                listOf(
//                    SauceEntity(1, "Tomato", "tomato, garlic, olive oil", "tomato"),
//                    SauceEntity(2, "Cheesy", "milk, parmesan, garlic", "cheese"),
//                    SauceEntity(3, "Carbonara", "egg, cheese, bacon", "carbonara")
//                )
//            )
//            fakeRepo.insertUser(
//                UserEntity("user1", "pass", isVegetarian = false, allergens = "milk")
//            )
//        }
//
//        viewModel = MainViewModel(
//            application = androidx.test.core.app.ApplicationProvider.getApplicationContext(),
//            repository = fakeRepo,
//            ioDispatcher = testDispatcher
//        )
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//    }
//
////    @Test
////    fun testFilterSaucesByAllergens() = runTest {
////        viewModel.loadAllSauces()
////        advanceUntilIdle()
////
////        val user = UserEntity(username = "user1", password = "pass", isVegetarian = false, allergens = "milk")
////        viewModel.updateUser(user)
////        advanceUntilIdle()
////
////        val filtered = viewModel.filteredSauces.value ?: emptyList()
////        val filteredNames = filtered.map { it.name }
////        println("Filtered sauces: $filteredNames")
////
////        assertFalse(filteredNames.contains("Cheesy"))
////        assertTrue(filteredNames.contains("Tomato"))
////    }
//
//    @Test
//    fun testToggleFavoriteAddsAndRemoves() = runTest {
//        val username = "user1"
//        val sauce = SauceEntity(id = 1, name = "Tomato", ingredients = "tomato", imageResName = "tomato")
//
//        viewModel.loadAllSauces()
//        advanceUntilIdle()
//
//        val observer = mock<Observer<List<SauceEntity>>>()
//        viewModel.favoriteSauces.observeForever(observer)
//
//        viewModel.loadFavorites(username)
//        advanceUntilIdle()
//        assertTrue(viewModel.favoriteSauces.value.isNullOrEmpty())
//
//        viewModel.toggleFavorite(username, sauce)
//        advanceUntilIdle()
//
//        val captor = argumentCaptor<List<SauceEntity>>()
//        verify(observer, atLeast(2)).onChanged(captor.capture())
//
//        val favAfterAdd = captor.allValues.last()
//        assertTrue(favAfterAdd.any { it.id == sauce.id })
//
//        viewModel.toggleFavorite(username, sauce)
//        advanceUntilIdle()
//
//        verify(observer, atLeast(3)).onChanged(captor.capture())
//
//        val favAfterRemove = captor.allValues.last()
//        assertFalse(favAfterRemove.any { it.id == sauce.id })
//
//        viewModel.favoriteSauces.removeObserver(observer)
//    }
//
//
//
//}
package com.example.pastatimer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.pastatimer.model.UserFavoriteSauceEntity
import com.example.pastatimer.repository.IAppRepository
import com.example.pastatimer.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: MainViewModel

    // Fake repository
    class FakeAppRepository : IAppRepository {
        private val sauces = mutableListOf<SauceEntity>()
        private val favorites = mutableListOf<UserFavoriteSauceEntity>()
        private val users = mutableListOf<UserEntity>()

        override suspend fun getAllPastaTypes(): List<PastaTypeEntity> = emptyList()

        override suspend fun getAllSauces(): List<SauceEntity> = sauces.toList()

        override suspend fun getFavoritesForUser(username: String): List<SauceEntity> {
            val favIds = favorites.filter { it.username == username }.map { it.sauceId }
            return sauces.filter { it.id in favIds }
        }

        override suspend fun getUserByUsername(username: String): UserEntity? =
            users.find { it.username == username }

        override suspend fun insertUser(user: UserEntity) {
            users.removeAll { it.username == user.username }
            users.add(user)
        }

        override suspend fun insertPastaTypes(types: List<PastaTypeEntity>) {}

        override suspend fun insertSauces(saucesList: List<SauceEntity>) {
            sauces.clear()
            sauces.addAll(saucesList)
        }

        override suspend fun addFavorite(username: String, sauceId: Int) {
            if (!favorites.any { it.username == username && it.sauceId == sauceId }) {
                favorites.add(UserFavoriteSauceEntity(username, sauceId))
            }
        }

        override suspend fun removeFavorite(username: String, sauceId: Int) {
            favorites.removeAll { it.username == username && it.sauceId == sauceId }
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val fakeRepo = FakeAppRepository()
        runTest {
            fakeRepo.insertSauces(
                listOf(
                    SauceEntity(1, "Tomato", "tomato, garlic, olive oil", "tomato"),
                    SauceEntity(2, "Cheesy", "milk, parmesan, garlic", "cheese"),
                    SauceEntity(3, "Carbonara", "egg, cheese, bacon", "carbonara")
                )
            )
            fakeRepo.insertUser(
                UserEntity("user1", "pass", isVegetarian = false, allergens = "milk")
            )
        }

        viewModel = MainViewModel(
            application = androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            repository = fakeRepo,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testToggleFavorite() = runTest {
        val username = "user1"
        val sauce = SauceEntity(1, "Tomato", "tomato, garlic, olive oil", "tomato")

        // Load all sauces first
        viewModel.loadAllSauces()
        advanceUntilIdle()

        // Load initial favorites (should be empty)
        viewModel.loadFavorites(username)
        advanceUntilIdle()

        // Assert initial state
        assertTrue("Initial favorites should be empty",
            viewModel.favoriteSauces.value.isNullOrEmpty())

        // Add sauce to favorites
        viewModel.toggleFavorite(username, sauce)
        advanceUntilIdle()

        // Assert sauce was added
        val favoritesAfterAdd = viewModel.favoriteSauces.value
        assertNotNull("Favorites should not be null after adding", favoritesAfterAdd)
        assertTrue("Sauce should be in favorites after adding",
            favoritesAfterAdd!!.any { it.id == sauce.id })

        // Remove sauce from favorites
        viewModel.toggleFavorite(username, sauce)
        advanceUntilIdle()

        // Assert sauce was removed
        val favoritesAfterRemove = viewModel.favoriteSauces.value
        assertNotNull("Favorites should not be null after removing", favoritesAfterRemove)
        assertFalse("Sauce should not be in favorites after removing",
            favoritesAfterRemove!!.any { it.id == sauce.id })
    }

    @Test
    fun testFilterSaucesByAllergens() = runTest {
        // Load all sauces
        viewModel.loadAllSauces()
        advanceUntilIdle()

        // Set user with milk allergy
        val user = UserEntity(
            username = "user1",
            password = "pass",
            isVegetarian = false,
            allergens = "milk"
        )
        viewModel.updateUser(user)
        advanceUntilIdle()

        // Check filtered sauces
        val filtered = viewModel.filteredSauces.value ?: emptyList()
        val filteredNames = filtered.map { it.name }

        // Should not contain "Cheesy" (has milk/cheese)
        assertFalse("Cheesy sauce should be filtered out due to milk allergy",
            filteredNames.contains("Cheesy"))

        // Should contain "Tomato" (no milk)
        assertTrue("Tomato sauce should not be filtered out",
            filteredNames.contains("Tomato"))
    }
}