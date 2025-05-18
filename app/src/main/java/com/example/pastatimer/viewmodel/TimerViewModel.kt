//package com.example.pastatimer.ui.login
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//
//class TimerViewModel : ViewModel() {
//
//    private val _timeLeft = MutableLiveData<Int>()
//    val timeLeft: LiveData<Int> get() = _timeLeft
//
//    private val _status = MutableLiveData<String>()
//    val status: LiveData<String> get() = _status
//
//    private var isCancelled = false
//    fun cancelTimer() {
//        isCancelled = true
//    }
//
//    fun startTimer(boilTime: Int) {
//        isCancelled = false
//        _timeLeft.value = boilTime * 60
//        viewModelScope.launch {
//            while (_timeLeft.value!! > 0 && !isCancelled) {
//                delay(1000L)
//                _timeLeft.value = _timeLeft.value!! - 1
//
//                val secondsLeft = _timeLeft.value!!
//                val total = boilTime * 60
//
//                _status.value = when {
//                    secondsLeft > total * 2 / 3 -> "Undercooked"
//                    secondsLeft > total / 3 -> "Al Dente"
//                    secondsLeft > 0 -> "Perfect"
//                    else -> "Overcooked"
//                }
//            }
//
//            if (!isCancelled) {
//                _status.value = "Overcooked"
//            }
//        }
//    }
//
//}
package com.example.pastatimer.viewmodel

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

    private var isRunning = false
    private var isCancelled = false

    fun startTimer(boilTime: Int) {
        if (isRunning) return
        isRunning = true
        isCancelled = false

        _timeLeft.value = boilTime * 60

        viewModelScope.launch {
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
        }
    }

    fun cancelTimer() {
        isCancelled = true
        isRunning = false
    }
}
