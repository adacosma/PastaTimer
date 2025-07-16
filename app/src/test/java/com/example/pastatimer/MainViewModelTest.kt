package com.example.pastatimer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pastatimer.model.UserFavoriteSauceEntity
import com.example.pastatimer.repository.IAppRepository
import com.example.pastatimer.viewmodel.AuthResult
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

    // Fake repository complet
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

        // ✅ Metodă lipsă adăugată
        override suspend fun updateUserPreferences(username: String, isVegetarian: Boolean, allergens: String) {
            users.find { it.username == username }?.let {
                users.remove(it)
                users.add(it.copy(isVegetarian = isVegetarian, allergens = allergens))
            }
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
    fun testFilterSaucesByAllergens() = runTest {
        viewModel.loadAllSauces()
        advanceUntilIdle()

        val user = UserEntity("user1", "pass", isVegetarian = false, allergens = "milk")
        viewModel.updateUser(user)
        advanceUntilIdle()

        val filtered = viewModel.filteredSauces.value ?: emptyList()
        val filteredNames = filtered.map { it.name }

        assertFalse("Cheesy sauce should be filtered out due to milk allergy", filteredNames.contains("Cheesy"))
        assertTrue("Tomato sauce should not be filtered out", filteredNames.contains("Tomato"))
    }

    @Test
    fun testLoginSuccess() = runTest {
        viewModel.login("user1", "pass")
        advanceUntilIdle()

        val result = viewModel.authLoginResult.value
        assertTrue(result is AuthResult.Success)
        assertEquals("user1", viewModel.user.value?.username)
    }

    @Test
    fun testLoginWrongPassword() = runTest {
        viewModel.login("user1", "wrongpass")
        advanceUntilIdle()

        val result = viewModel.authLoginResult.value
        assertTrue(result is AuthResult.Error)
        assertEquals("Incorrect password", (result as AuthResult.Error).message)
    }

    @Test
    fun testToggleFavoriteAddsAndRemovesSauce() = runTest {
        val user = "user1"
        val sauce = SauceEntity(1, "Tomato", "tomato, garlic, olive oil", "tomato")

        viewModel.toggleFavorite(user, sauce)
        viewModel.loadFavorites(user) // <- corect
        advanceUntilIdle()

        val afterAdd = viewModel.favoriteSauces.value?.map { it.name } ?: emptyList()
        assertTrue("Tomato should be added as favorite", afterAdd.contains("Tomato"))

        viewModel.toggleFavorite(user, sauce)
        viewModel.loadFavorites(user) // <- corect
        advanceUntilIdle()

        val afterRemove = viewModel.favoriteSauces.value?.map { it.name } ?: emptyList()
        assertFalse("Tomato should be removed from favorites", afterRemove.contains("Tomato"))
    }

    @Test
    fun testStartTimerSetsTimeLeft() = runTest {
        val boilTime = 1 // 1 minut

        viewModel.startTimer(boilTime)
        advanceTimeBy(0L) // pornește imediat

        val expected = boilTime * 60
        assertEquals(expected, viewModel.timeLeft.value)
    }

    @Test
    fun testResetTimerResetsTimeLeftAndStatus() = runTest {
        viewModel.resetTimer(1)
        runCurrent() // sau advanceTimeBy(0L)

        assertEquals(60, viewModel.timeLeft.value)
        assertEquals("Starting...", viewModel.status.value)
    }

    @Test
    fun testCancelTimerStopsCountdown() = runTest {
        viewModel.startTimer(1) // 1 minut
        advanceTimeBy(2000L) // 2 secunde
        viewModel.cancelTimer()
        val timeAfterCancel = viewModel.timeLeft.value!!

        advanceTimeBy(3000L) // încă 3 secunde
        assertEquals(timeAfterCancel, viewModel.timeLeft.value)
    }



}
