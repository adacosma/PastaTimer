package com.example.pastatimer.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Composable screen that displays the **main menu** of the application
 * after a successful login.
 *
 * The screen greets the user and provides buttons for navigating to:
 * - Pasta selection screen
 * - Sauce selection screen
 * - Favourite recipes screen (currently unimplemented)
 * - Preferences screen (to change allergens and vegetarian settings)
 *
 * @param username The username of the logged-in user, passed to destination routes.
 */

@Composable
fun MainMenu(navController: NavController, username: String) {

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { navController.navigate("pasta/${username}") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Select Pasta")
        }

        Button(
            onClick = { navController.navigate("sauce/${username}") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Select Sauce")
        }

        Button(
            onClick = {
                println("Navigating to favorites") // pentru test
                navController.navigate("favorites/$username")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("View Favourites")
        }


        Button(
            onClick = { navController.navigate("allergens/$username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Change Preferences")
        }
    }
}