package com.example.pastatimer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.pastatimer.ui.login.TimerViewModel

@Composable
fun TimerScreen(
    pastaName: String,
    boilTime: Int,
    username: String,
    navController: NavController,
    viewModel: TimerViewModel = viewModel()
) {
    val timeLeft by viewModel.timeLeft.observeAsState(initial = boilTime * 60)
    val status by viewModel.status.observeAsState(initial = "Starting...")

    // Pornește timerul o singură dată
    LaunchedEffect(Unit) {
        viewModel.startTimer(boilTime)
        delay((boilTime * 60 + 2).toLong() * 1000) // Așteaptă până la final + 2 secunde
        navController.navigate("sauce/$username") {
            popUpTo("home/$username") { inclusive = false }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⏱ Timer for $pastaName", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Time left: ${timeLeft / 60}:${(timeLeft % 60).toString().padStart(2, '0')}",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Status: $status", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("⬅ Stop and go back")
        }
    }
}
