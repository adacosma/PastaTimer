
package com.example.pastatimer.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.pastatimer.PastaTypeEntity
import com.example.pastatimer.SauceEntity
import com.example.pastatimer.UserEntity
import com.example.pastatimer.defaultPastaList
import com.example.pastatimer.defaultSauceList
import com.example.pastatimer.repository.AppRepository
import com.example.pastatimer.repository.IAppRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

class MainViewModel(
    application: Application,
    private val repository: IAppRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {

    fun populateDatabaseIfEmpty() {
        viewModelScope.launch(ioDispatcher) {
            val pastaList = repository.getAllPastaTypes()
            val sauceList = repository.getAllSauces()

            if (pastaList.isEmpty()) {
                repository.insertPastaTypes(defaultPastaList)
            }

            if (sauceList.isEmpty()) {
                repository.insertSauces(defaultSauceList)
            }
        }
    }


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

    // LiveData for login results
    private val _authLoginResult = MutableLiveData<AuthResult>()
    val authLoginResult: LiveData<AuthResult> = _authLoginResult

    // LiveData for signup results
    private val _authSignUpResult = MutableLiveData<AuthResult>()
    val authSignUpResult: LiveData<AuthResult> = _authSignUpResult

    fun login(username: String, password: String) {
        // validate input
        if (username.isBlank() || password.isBlank()) {
            _authLoginResult.value = AuthResult.Error("Username and password cannot be empty")
            return
        }

        _authLoginResult.value = AuthResult.Loading


        viewModelScope.launch {
            try {
                val user = repository.getUserByUsername(username)
                when {
                    user == null -> {
                        _authLoginResult.value = AuthResult.Error("Username not found")
                    }
                    user.password != password -> {
                        _authLoginResult.value = AuthResult.Error("Incorrect password")
                    }
                    else -> {
                        _user.value = user // set current user
                        _authLoginResult.value = AuthResult.Success
                    }
                }
            } catch (e: Exception) {
                _authLoginResult.value = AuthResult.Error("Login failed: ${e.message}")
            }
        }
    }

    fun signUp(username: String, password: String, confirmPassword: String) {
        // validate input
        when {
            username.isBlank() -> {
                _authSignUpResult.value = AuthResult.Error("Username cannot be empty")
                return
            }
            password.isBlank() -> {
                _authSignUpResult.value = AuthResult.Error("Password cannot be empty")
                return
            }
            password != confirmPassword -> {
                _authSignUpResult.value = AuthResult.Error("Passwords do not match")
                return
            }
            password.length < 4 -> {
                _authSignUpResult.value = AuthResult.Error("Password must be at least 4 characters")
                return
            }
        }

        _authSignUpResult.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                val existingUser = repository.getUserByUsername(username)
                if (existingUser != null) {
                    _authSignUpResult.value = AuthResult.Error("Username already exists")
                } else {
                    val newUser = UserEntity(
                        username = username,
                        password = password,
                        isVegetarian = false,
                        allergens = ""
                    )
                    repository.insertUser(newUser)
                    _authSignUpResult.value = AuthResult.Success
                }
            } catch (e: Exception) {
                _authSignUpResult.value = AuthResult.Error("Registration failed: ${e.message}")
            }
        }
    }

    fun updateUserPreferences(username: String, isVegetarian: Boolean, allergens: String) {
        viewModelScope.launch {
            // Folosește metoda specifică de update
            repository.updateUserPreferences(username, isVegetarian, allergens)

            // Reîncarcă user-ul
            val updatedUser = repository.getUserByUsername(username)
            _user.value = updatedUser
            filterSauces()
        }
    }

    // Încarcă toate tipurile de paste
    fun loadPastaTypes() {
        viewModelScope.launch(Dispatchers.IO) {
            val types = repository.getAllPastaTypes()
            _pastaTypes.postValue(types)
        }
    }

    // Gaseste sauce dupa nume
    fun getSauceByName(name: String): LiveData<SauceEntity?> {
        return MutableLiveData(allSauces.value?.find { it.name == name })
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

    fun toggleFavorite(username: String, sauce: SauceEntity) {
        viewModelScope.launch(ioDispatcher) {
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

    fun loadUserAndSauces(username: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username) ?: return@launch
            _user.value = user

            val sauces = withContext(Dispatchers.IO) {
                repository.getAllSauces()
            }
            _allSauces.value = sauces

            // apel corect care folosește alergeni + vegetarian
            filterSauces()

            loadFavorites(username)
        }
    }



    // Obține un utilizator după username
    suspend fun getUserByUsername(username: String): UserEntity? {
        return repository.getUserByUsername(username)
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