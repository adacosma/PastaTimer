package com.example.pastatimer.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TimerViewModel : ViewModel() {

    private val _timeLeft = MutableLiveData<Int>()
    val timeLeft: LiveData<Int> get() = _timeLeft

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    fun startTimer(boilTime: Int) {
        _timeLeft.value = boilTime * 60
        viewModelScope.launch {
            while (_timeLeft.value!! > 0) {
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
            _status.value = "Overcooked"
        }
    }
}
