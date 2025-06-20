package com.example.pastatimer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Job

/**
 * ViewModel responsible for managing the pasta cooking timer.
 *
 * Exposes LiveData for time left and cooking status ("Undercooked", "Al Dente", "Perfect", "Overcooked"),
 * and provides methods to start, cancel, reset, or conditionally restart the timer based on pasta type.
 *
 * The timer runs in a coroutine and updates values every second.
 */
class TimerViewModel : ViewModel() {

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


}
