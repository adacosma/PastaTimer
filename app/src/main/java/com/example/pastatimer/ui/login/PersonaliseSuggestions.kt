package com.example.pastatimer.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.pastatimer.AppDatabase

/**
 * Composable function that displays the personalization screen
 * where the user can select allergens and dietary preferences.
 *
 * Loads existing preferences from the Room database and updates them when saved.
 *
 * @param navController The navigation controller used to move between screens.
 * @param username The currently logged-in user's username, used to load and update preferences.
 */
@Composable
fun PersonaliseSuggestions(navController: NavController, username: String) {
    val allergensList = listOf("Milk", "Eggs", "Nuts", "Soy", "Fish")
    val selectedAllergens = remember { mutableStateListOf<String>() }
    var isVegetarian by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val user = userDao.getUserByUsername(username)
        user?.let {
            isVegetarian = it.isVegetarian
            selectedAllergens.clear()
            selectedAllergens.addAll(it.allergens.split(",").filter { it.isNotBlank() })
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
            coroutineScope.launch {
                val allergensString = selectedAllergens.joinToString(",")
                userDao.updatePreferences(
                    username = username,
                    isVegetarian = isVegetarian,
                    allergens = allergensString
                )
                val updatedUser = userDao.getUserByUsername(username)
                navController.navigate("home/${username}")
            }
        }) {
            Text("Save Preferences")
        }
    }
}