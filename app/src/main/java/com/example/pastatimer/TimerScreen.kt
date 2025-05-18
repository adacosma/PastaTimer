//package com.example.pastatimer
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import kotlinx.coroutines.delay
//import com.example.pastatimer.ui.login.TimerViewModel
//
//@Composable
//fun TimerScreen(
//    pastaName: String,
//    boilTime: Int,
//    username: String,
//    navController: NavController,
//    viewModel: TimerViewModel = viewModel()
//) {
//    val timeLeft by viewModel.timeLeft.observeAsState(initial = boilTime * 60)
//    val status by viewModel.status.observeAsState(initial = "Starting...")
//
//    // Pornește timerul o singură dată
//    LaunchedEffect(Unit) {
//        viewModel.startTimer(boilTime)
//        delay((boilTime * 60 + 2).toLong() * 1000) // Așteaptă până la final + 2 secunde
//        navController.navigate("sauce/$username") {
//            popUpTo("home/$username") { inclusive = false }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("⏱ Timer for $pastaName", style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(20.dp))
//        Text(
//            "Time left: ${timeLeft / 60}:${(timeLeft % 60).toString().padStart(2, '0')}",
//            style = MaterialTheme.typography.headlineLarge
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Text("Status: $status", style = MaterialTheme.typography.titleMedium)
//        Spacer(modifier = Modifier.height(24.dp))
//        Button(onClick = { navController.popBackStack() }) {
//            Text("⬅ Stop and go back")
//        }
//    }
//}
package com.example.pastatimer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    // Culoare în funcție de status
    val backgroundColor = when (status) {
        "Undercooked" -> Color(0xFFFFCDD2) // roșu deschis
        "Al Dente" -> Color(0xFFFFF9C4)     // galben pal
        "Perfect" -> Color(0xFFC8E6C9)      // verde pal
        "Overcooked" -> Color(0xFFBDBDBD)   // gri închis
        else -> MaterialTheme.colorScheme.background
    }

    // Pornește timerul
    LaunchedEffect(Unit) {
        viewModel.startTimer(boilTime)
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

        // Buton: Stop Cooking
        Button(
            onClick = {
                navController.navigate("sauce/$username") {
                    popUpTo("home/$username") { inclusive = false }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("🍝 Stop Cooking and go to Sauces", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buton: back
        OutlinedButton(onClick = { navController.popBackStack() }) {
            Text("⬅ Back")
        }
    }
}
