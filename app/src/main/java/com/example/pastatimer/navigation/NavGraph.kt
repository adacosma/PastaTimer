package com.example.pastatimer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pastatimer.AppDatabase
import com.example.pastatimer.SauceDetailsScreen
import com.example.pastatimer.SauceScreen
import com.example.pastatimer.TimerScreen
import com.example.pastatimer.UserEntity
import com.example.pastatimer.defaultPastaList
import com.example.pastatimer.defaultSauceList
import com.example.pastatimer.ui.login.LogInScreen
import com.example.pastatimer.ui.login.PersonaliseSuggestions
import com.example.pastatimer.ui.login.SignUpScreen
import com.example.pastatimer.ui.menu.MainMenu
import com.example.pastatimer.ui.screens.PastaScreen
import com.example.pastatimer.viewmodel.TimerViewModel
import com.example.pastatimer.FavoriteSauceScreen


@Composable
fun NavGraph() {
    val navController = rememberNavController()
    // Create a single instance of the ViewModel that will be shared across destinations
    val timerViewModel: TimerViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LogInScreen(navController) }

        composable("sign up") { SignUpScreen(navController) }

        composable("allergens/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            PersonaliseSuggestions(navController, username)
        }

        composable("home/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            MainMenu(navController, username)
        }

        composable("pasta/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)
            val userDao = db.userDao()
            val user = remember { mutableStateOf<UserEntity?>(null) }

            LaunchedEffect(username) {
                user.value = userDao.getUserByUsername(username)
            }

            user.value?.let {
                PastaScreen(pastas = defaultPastaList, navController = navController, user = it)
            }
        }

        composable("sauce/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)
            val userDao = db.userDao()
            val user = remember { mutableStateOf<UserEntity?>(null) }

            LaunchedEffect(username) {
                user.value = userDao.getUserByUsername(username)
            }

            user.value?.let {
                SauceScreen(navController = navController, user = it)
            }
        }

        composable("details/{sauceName}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("sauceName") ?: ""
            val sauce = defaultSauceList.find { it.name == name }
            sauce?.let {
                SauceDetailsScreen(sauce = it, navController = navController)
            }
        }

        composable("timer/{name}/{boilTime}/{username}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: "Unknown"
            val boilTime = backStackEntry.arguments?.getString("boilTime")?.toIntOrNull() ?: 0
            val username = backStackEntry.arguments?.getString("username") ?: ""

            // Use the shared ViewModel instance that persists across navigation
            TimerScreen(
                pastaName = name,
                boilTime = boilTime,
                username = username,
                navController = navController,
                viewModel = timerViewModel
            )
        }
//        composable("favorites") {
//            FavoriteSauceScreen(navController = navController)
//        }

        composable("favorites/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            FavoriteSauceScreen(navController = navController, username = username)
        }


    }
}