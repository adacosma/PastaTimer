package com.example.pastatimer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pastatimer.viewmodel.TimerViewModel

/**
 * Composable screen that displays a countdown timer for cooking pasta.
 *
 * Shows the time remaining, a status message (e.g., "Al Dente", "Perfect"),
 * and changes background color based on the current status.
 *
 * Provides buttons to:
 * - Stop cooking and go to the sauce selection screen
 * - Reset the timer
 * - Navigate back
 *
 * The timer logic is handled by a shared [TimerViewModel], which manages LiveData updates.
 *
 * @param pastaName The name of the selected pasta.
 * @param boilTime The boiling time in minutes for the selected pasta.
 * @param username The currently logged-in user's username (used for navigation).
 * @param navController The navigation controller used for screen transitions.
 * @param viewModel The shared ViewModel managing the timer state.
 */
@Composable
fun TimerScreen(
    pastaName: String,
    boilTime: Int,
    username: String,
    navController: NavController,
    viewModel: TimerViewModel
) {
    val timeLeft by viewModel.timeLeft.observeAsState(boilTime * 60)
    val status by viewModel.status.observeAsState("Starting...")

    LaunchedEffect(pastaName, boilTime) {
        viewModel.forceRestartTimer(pastaName, boilTime)
    }

    val backgroundColor = when (status) {
        "Undercooked" -> Color(0xFFFFCDD2)
        "Al Dente" -> Color(0xFFFFF9C4)
        "Perfect" -> Color(0xFFC8E6C9)
        "Overcooked" -> Color(0xFFBDBDBD)
        else -> MaterialTheme.colorScheme.background
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
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
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.cancelTimer()
                navController.navigate("sauce/$username") {
                    popUpTo("home/$username") { inclusive = false }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("⛔ Stop Cooking and go to Sauces", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.resetTimer(boilTime) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("🔄 Reset Timer", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = { navController.popBackStack() }) {
            Text("⬅ Back")
        }
    }
}
