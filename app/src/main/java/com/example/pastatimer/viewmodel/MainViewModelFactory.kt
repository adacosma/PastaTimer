package com.example.pastatimer.viewmodel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pastatimer.repository.AppRepository

/**
 * Factory class used to instantiate [MainViewModel] with a custom constructor.
 *
 * This is necessary because [MainViewModel] requires dependencies
 * (an [Application] instance and an [AppRepository]) which cannot be provided
 * by the default ViewModelProvider.
 *
 * This pattern follows best practices for dependency injection
 * in Android MVVM architecture.
 */
class MainViewModelFactory(
    private val application: Application,
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the requested ViewModel.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A newly created ViewModel instance, casted to T.
     * @throws IllegalArgumentException if the ViewModel class is unknown.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
