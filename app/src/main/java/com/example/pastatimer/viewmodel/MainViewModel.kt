package com.example.pastatimer.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.pastatimer.PastaTypeEntity
import com.example.pastatimer.SauceEntity
import com.example.pastatimer.UserEntity
import com.example.pastatimer.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {

    // LiveData pentru tipurile de paste
    private val _pastaTypes = MutableLiveData<List<PastaTypeEntity>>()
    val pastaTypes: LiveData<List<PastaTypeEntity>> = _pastaTypes

    // LiveData pentru sosurile favorite ale utilizatorului
    private val _favoriteSauces = MutableLiveData<List<SauceEntity>>()
    val favoriteSauces: LiveData<List<SauceEntity>> = _favoriteSauces

    // LiveData pentru utilizatorul curent
    private val _user = MutableLiveData<UserEntity?>()
    val user: LiveData<UserEntity?> = _user

    // LiveData pentru toate sosurile
    private val _allSauces = MutableLiveData<List<SauceEntity>>()
    val allSauces: LiveData<List<SauceEntity>> = _allSauces

    private val _filteredSauces = MutableLiveData<List<SauceEntity>>()
    val filteredSauces: LiveData<List<SauceEntity>> = _filteredSauces


    // Încarcă toate tipurile de paste
    fun loadPastaTypes() {
        viewModelScope.launch(Dispatchers.IO) {
            val types = repository.getAllPastaTypes()
            _pastaTypes.postValue(types)
        }
    }

    // Încarcă sosurile favorite ale utilizatorului
    fun loadFavorites(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites = repository.getFavoritesForUser(username)
            _favoriteSauces.postValue(favorites)
        }
    }

    // Încarcă toate sosurile
    fun loadAllSauces() {
        viewModelScope.launch(Dispatchers.IO) {
            val sauces = repository.getAllSauces()
            _allSauces.postValue(sauces)

            // Apelezi filtrarea pe firul principal
            withContext(Dispatchers.Main) {
                filterSauces()
            }
        }
    }


    // Adaugă sau elimină un sos din favorite
    fun toggleFavorite(username: String, sauce: SauceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFavorite = repository.getFavoritesForUser(username).any { it.id == sauce.id }
            if (isFavorite) {
                repository.removeFavorite(username, sauce.id)
            } else {
                repository.addFavorite(username, sauce.id)
            }
            loadFavorites(username)
        }
    }

    // Actualizează utilizatorul curent
    fun updateUser(userEntity: UserEntity) {
        _user.value = userEntity
        filterSauces()
    }


    // Obține un utilizator după username
    suspend fun getUserByUsername(username: String): UserEntity? {
        return repository.getUserByUsername(username)
    }

    // Inserează un nou utilizator
    fun insertUser(user: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(user)
        }
    }

    // Inserează tipuri de paste
    fun insertPastaTypes(types: List<PastaTypeEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPastaTypes(types)
        }
    }

    // Inserează sosuri
    fun insertSauces(sauces: List<SauceEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSauces(sauces)
        }
    }

    private val _timeLeft = MutableLiveData<Int>()
    val timeLeft: LiveData<Int> get() = _timeLeft

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private var isRunning = false
    private var isCancelled = false
    private var timerJob: Job? = null
    private var currentPastaName: String? = null
    private var currentBoilTime: Int? = null

    fun startTimer(boilTime: Int) {
        if (isRunning) return
        isRunning = true
        isCancelled = false

        if (_timeLeft.value == null) {
            _timeLeft.value = boilTime * 60
        }

        timerJob = viewModelScope.launch {
            while (_timeLeft.value!! > 0 && !isCancelled) {
                delay(1000L)
                _timeLeft.value = _timeLeft.value!! - 1

                val secondsLeft = _timeLeft.value!!
                val total = boilTime * 60

                _status.value = when {
                    secondsLeft > total * 2 / 3 -> "Undercooked"
                    secondsLeft > total / 3 -> "Al Dente"
                    secondsLeft > 0 -> "Perfect"
                    else -> "Overcooked"
                }
            }

            if (!isCancelled) {
                _status.value = "Overcooked"
            }

            isRunning = false
        }
    }

    fun cancelTimer() {
        isCancelled = true
        isRunning = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer(boilTime: Int) {
        cancelTimer() // oprește complet coroutinea anterioară
        _timeLeft.value = boilTime * 60
        _status.value = "Starting..."
        startTimer(boilTime)
    }

    fun forceRestartTimer(pastaName: String, boilTime: Int) {
        if (pastaName == currentPastaName && boilTime == currentBoilTime) {
            // Același tip de paste – nu resetăm timerul
            return
        }

        // Tipul de paste s-a schimbat – resetăm
        currentPastaName = pastaName
        currentBoilTime = boilTime
        cancelTimer()
        _timeLeft.value = boilTime * 60
        _status.value = "Starting..."
        startTimer(boilTime)
    }

    private fun filterSauces() {
        val currentUser = _user.value ?: return
        val sauces = _allSauces.value ?: return

        val allergens = currentUser.allergens
            .split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }

        val vegetarianOnly = currentUser.isVegetarian

        _filteredSauces.value = sauces.filter { sauce ->
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