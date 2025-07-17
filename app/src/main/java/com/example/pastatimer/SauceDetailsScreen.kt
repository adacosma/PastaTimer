package com.example.pastatimer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pastatimer.viewmodel.MainViewModel
import androidx.compose.runtime.livedata.observeAsState

/**
 * Composable screen that displays detailed information for a given sauce.
 *
 * Follows the MVVM architecture:
 * - Uses LiveData from MainViewModel to load the sauce from Room database
 * - Automatically updates the UI when data becomes available
 * - Displays image, name, and list of ingredients
 *
 * @param sauceName Name of the sauce to be displayed.
 * @param navController Navigation controller for screen transitions.
 * @param viewModel The shared MainViewModel instance.
 */
@Composable
fun SauceDetailsScreen(
    sauceName: String,
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val sauce by viewModel.getSauceByName(sauceName).observeAsState()

    if (sauce == null) {
        // Afișăm un loader până se încarcă sosul
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Doar după ce sauce e valid
    val imageId = remember(sauce!!.imageResName) {
        context.resources.getIdentifier(sauce!!.imageResName, "drawable", context.packageName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(34.dp))

        Text(
            text = sauce!!.name,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))
        if (imageId != 0) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = sauce!!.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Ingredients:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column {
            sauce!!.ingredients.split(",").forEach { ingredient ->
                Text(
                    text = "• ${ingredient.trim()}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("⬅ Back")
        }
    }
}
