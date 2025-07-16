package com.example.pastatimer.navigation

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.pastatimer.*
import com.example.pastatimer.repository.AppRepository
import com.example.pastatimer.ui.login.*
import com.example.pastatimer.ui.menu.MainMenu
import com.example.pastatimer.ui.screens.PastaScreen
import com.example.pastatimer.viewmodel.*
import androidx.lifecycle.ViewModelProvider

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as Application

    val db = AppDatabase.getDatabase(context)
    val repository = remember {
        AppRepository(

            db.userDao(),
            db.pastaTypeDao(),
            db.sauceDao(),
            db.userFavoriteSauceDao()
        )
    }

    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(context, repository)
    )

    LaunchedEffect(Unit) {
        mainViewModel.populateDatabaseIfEmpty()
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LogInScreen(navController, mainViewModel)
        }
        composable("sign up") {
            SignUpScreen(navController, mainViewModel)
        }

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
            PastaScreen(navController = navController, username = username, viewModel = mainViewModel)
        }

        composable("sauce/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val userState = remember { mutableStateOf<UserEntity?>(null) }

            LaunchedEffect(username) {
                val user = mainViewModel.getUserByUsername(username)
                userState.value = user
                mainViewModel.updateUser(user ?: return@LaunchedEffect)
            }

            userState.value?.let {
                SauceScreen(navController = navController, user = it, viewModel = mainViewModel)
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

            TimerScreen(
                pastaName = name,
                boilTime = boilTime,
                username = username,
                navController = navController,
                viewModel = mainViewModel
            )
        }

        composable("favorites/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            FavoriteSauceScreen(navController = navController, username = username, viewModel = mainViewModel)
        }
    }
}
