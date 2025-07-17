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

/**
 * This composable sets up the entire Navigation Graph for the PastaTimer app.
 * It initializes the database and ViewModel (MVVM), and defines all the screens.
 */
@Composable
fun NavGraph() {
    // Create the NavController that manages the back stack of destination
    val navController = rememberNavController()
    // Get application context to initialize the Room database
    val context = LocalContext.current.applicationContext as Application

    // Instantiate the Room database and repository (Repository layer from MVVM)
    val db = AppDatabase.getDatabase(context)
    val repository = remember {
        AppRepository(

            db.userDao(),
            db.pastaTypeDao(),
            db.sauceDao(),
            db.userFavoriteSauceDao()
        )
    }

    // Create the MainViewModel with a factory to inject dependencies (ViewModel from MVVM)
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(context, repository)
    )

    // Populate Room database with default pasta/sauce if empty (executed once on launch)
    LaunchedEffect(Unit) {
        mainViewModel.populateDatabaseIfEmpty()
    }

    // Define the navigation graph with all the app's routes
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LogInScreen(navController, mainViewModel)
        }
        composable("sign up") {
            SignUpScreen(navController, mainViewModel)
        }

        composable("allergens/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            PersonaliseSuggestions(navController, username, mainViewModel)
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
            SauceScreen(navController, username, mainViewModel)
        }


        composable("details/{sauceName}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("sauceName") ?: ""
            SauceDetailsScreen(name, navController, mainViewModel )
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
