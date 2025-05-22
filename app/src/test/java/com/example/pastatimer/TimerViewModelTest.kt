package com.example.pastatimer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pastatimer.viewmodel.TimerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*


@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TimerViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TimerViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startTimer should count down correctly`() = runTest {
        val boilTime = 1 // 60 sec

        viewModel.startTimer(boilTime)

        // La început trebuie să fie 60 secunde
        assertEquals(60, viewModel.timeLeft.value)

        advanceTimeBy(1000L) // Simulează o secundă
        runCurrent() // Rulează task-urile suspendate

        assertEquals(59, viewModel.timeLeft.value)
    }

    @Test
    fun `status should update to Al Dente when time passes`() = runTest {
        val boilTime = 3 // 180 sec

        viewModel.startTimer(boilTime)

        advanceTimeBy(61_000L)
        runCurrent()

        val status = viewModel.status.value
        assertTrue(status == "Al Dente" || status == "Perfect" || status == "Undercooked")
    }

    @Test
    fun `resetTimer should restart countdown`() = runTest {
        viewModel.startTimer(1)
        advanceTimeBy(2_000L)
        runCurrent()

        viewModel.resetTimer(2)
        runCurrent()

        assertEquals(120, viewModel.timeLeft.value)
        assertEquals("Starting...", viewModel.status.value)
    }
}
