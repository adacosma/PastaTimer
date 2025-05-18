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
import com.example.pastatimer.UserEntity
import com.example.pastatimer.viewmodel.SauceViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import com.example.pastatimer.AppDatabase
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*


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

            if (user.value != null) {
                PastaScreen(pastas = defaultPastaList, navController = navController, user = user.value!!
                )
            }
        }

        composable("sauce/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)
            val userDao = db.userDao()
            val user: MutableState<UserEntity?> = remember { mutableStateOf(null) }

            LaunchedEffect(username) {
                user.value = userDao.getUserByUsername(username)
            }


            if (user.value != null) {
                SauceScreen(navController = navController, user = user.value!!)
            }
        }


        composable("details/{sauceName}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("sauceName") ?: ""
            val sauce = defaultSauceList.find { sauceItem -> sauceItem.name == name }
            if (sauce != null) {
                SauceDetailsScreen(sauce = sauce, navController = navController)
            }

        }

    }
}
