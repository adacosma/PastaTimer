package com.example.pastatimer.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pastatimer.viewmodel.MainViewModel
import kotlinx.coroutines.launch

/**
 * Composable function that renders a screen allowing the user to personalize
 * their dietary preferences and allergens following MVVM pattern.
 *
 * Uses MainViewModel to handle preference updates and user data loading.
 *
 * @param username The username of the currently logged-in user.
 * @param navController Navigation controller for screen transitions.
 * @param viewModel MainViewModel for handling preferences logic.
 */

@Composable
fun PersonaliseSuggestions(
    navController: NavController,
    username: String,
    viewModel: MainViewModel
) {
    val allergensList = listOf("Milk", "Eggs", "Nuts", "Soy", "Fish")
    val selectedAllergens = remember { mutableStateListOf<String>() }
    var isVegetarian by remember { mutableStateOf(false) }

    // get current user from viewModel
    val currentUser by viewModel.user.observeAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // get current preferences
    LaunchedEffect(username) {
        if (currentUser?.username != username) {
            val user = viewModel.getUserByUsername(username)
            user?.let { viewModel.updateUser(it) }
        }
    }

    // update ui based on current user preferences
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            isVegetarian = user.isVegetarian
            selectedAllergens.clear()
            selectedAllergens.addAll(
                user.allergens.split(",").filter { it.isNotBlank() }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Personalise Suggestions",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Text(
            text = "Select allergens",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        allergensList.forEach { allergen ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = allergen in selectedAllergens,
                    onCheckedChange = { checked ->
                        if (checked) selectedAllergens.add(allergen)
                        else selectedAllergens.remove(allergen)
                    }
                )
                Text(text = allergen)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Vegetarian",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isVegetarian,
                onCheckedChange = { isVegetarian = it }
            )
            Text("I prefer no meat recipes")
        }

        Button(onClick = {
            val allergensString = selectedAllergens.joinToString(",")
            viewModel.updateUserPreferences(username, isVegetarian, allergensString)

            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Preferences saved successfully!",
                    duration = SnackbarDuration.Short
                )
                kotlinx.coroutines.delay(300)
                navController.navigate("home/${username}")
            }
        }) {
            Text("Save Preferences")
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}