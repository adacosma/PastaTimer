import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pastatimer.viewmodel.TimerViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import org.junit.After

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TimerViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TimerViewModel()
    }

    @Test
    fun `timer starts and counts down`() = runTest {
        val boilTime = 1
        viewModel.startTimer(boilTime)

        advanceTimeBy(1000L)
        assertEquals(59, viewModel.timeLeft.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
