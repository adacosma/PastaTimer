package com.example.pastatimer.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.pastatimer.*
import com.example.pastatimer.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    // LiveData observabilă
    private val _favoriteSauces = MutableLiveData<List<SauceEntity>>()
    val favoriteSauces: LiveData<List<SauceEntity>> = _favoriteSauces

    private val _user = MutableLiveData<UserEntity?>()
    val user: LiveData<UserEntity?> = _user

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(
            db.sauceDao(),
            db.userDao(),
            db.pastaTypeDao(),
            db.userFavoriteSauceDao()
        )
    }

    fun loadFavorites(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites = repository.getFavoritesForUser(username)
            _favoriteSauces.postValue(favorites)
        }
    }

    fun toggleFavorite(username: String, sauce: SauceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getFavoritesForUser(username).any { it.id == sauce.id }) {
                repository.removeFavorite(username, sauce.id)
            } else {
                repository.addFavorite(username, sauce.id)
            }
            loadFavorites(username)
        }
    }

    fun updateUser(userEntity: UserEntity) {
        _user.value = userEntity
    }

    fun getAllSauces(): List<SauceEntity> {
        return repository.getAllSauces()
    }

    fun getAllPastaTypes(): List<PastaTypeEntity> {
        return repository.getAllPastaTypes()
    }
}
