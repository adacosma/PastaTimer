package com.example.pastatimer
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(
    pastaName: String,
    boilTime: Int,
    navController: NavController
) {
    var timeLeft by remember { mutableStateOf(boilTime * 60) } // în secunde
    var status by remember { mutableStateOf("Starting...") }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
            status = when {
                timeLeft > boilTime * 60 * 2 / 3 -> "Undercooked"
                timeLeft > boilTime * 60 / 3 -> "Al Dente"
                timeLeft > 0 -> "Perfect"
                else -> "Overcooked"
            }
        }
        status = "Overcooked"
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⏱ Timer for $pastaName", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Time left: ${timeLeft / 60}:${(timeLeft % 60).toString().padStart(2, '0')}",
            style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Status: $status", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("⬅ Stop and go back")
        }
    }
}
