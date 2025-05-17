package com.example.pastatimer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pastatimer.SauceScreen
import com.example.pastatimer.defaultPastaList
import com.example.pastatimer.defaultSauceList
import com.example.pastatimer.ui.login.LogInScreen
import com.example.pastatimer.ui.menu.MainMenu
import com.example.pastatimer.ui.screens.PastaScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LogInScreen(navController) }
        composable("home") { MainMenu(navController) }
        composable("pasta") { PastaScreen(defaultPastaList) }
        composable("sauce") { SauceScreen(defaultSauceList) }
    }
}
