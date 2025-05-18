package com.example.pastatimer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pastatimer.SauceDetailsScreen
import com.example.pastatimer.SauceScreen
import com.example.pastatimer.defaultPastaList
import com.example.pastatimer.defaultSauceList
import com.example.pastatimer.ui.login.LogInScreen
import com.example.pastatimer.ui.login.PersonaliseSuggestions
import com.example.pastatimer.ui.login.SignUpScreen
import com.example.pastatimer.ui.menu.MainMenu
import com.example.pastatimer.ui.screens.PastaScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LogInScreen(navController) }
        composable("sign up") { SignUpScreen(navController) }
        composable("allergens/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            PersonaliseSuggestions(navController, username)
        }

        composable("home/{username}") {backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            MainMenu(navController, username)

        }
        composable("pasta") { PastaScreen(defaultPastaList,  navController = navController) }
        composable("sauce") { SauceScreen(defaultSauceList, navController = navController )}

        composable("details/{sauceName}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("sauceName") ?: ""
            val sauce = defaultSauceList.find { it.name == name }
            if (sauce != null) {
                SauceDetailsScreen(sauce = sauce, navController = navController)
            }

        }

    }
}
