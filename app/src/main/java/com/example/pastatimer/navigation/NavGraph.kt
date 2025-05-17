package com.example.pastatimer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pastatimer.ui.login.LogInScreen
import com.example.pastatimer.ui.menu.MainMenu

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LogInScreen(navController)}
        composable("home") { MainMenu() }
    }
}
