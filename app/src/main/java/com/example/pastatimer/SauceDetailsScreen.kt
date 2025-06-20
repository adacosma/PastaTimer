package com.example.pastatimer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Composable screen that displays detailed information about a selected sauce.
 *
 * Shows the sauce name, image, and a list of its ingredients.
 * Includes a back button to return to the previous screen.
 *
 * @param sauce The [SauceEntity] object containing sauce details.
 * @param navController Navigation controller used to return to the previous screen.
 */
@Composable
fun SauceDetailsScreen(sauce: SauceEntity, navController: NavController) {
    val context = LocalContext.current
    val imageId = remember(sauce.imageResName) {
        context.resources.getIdentifier(sauce.imageResName, "drawable", context.packageName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(34.dp))
        Text(
            text = sauce.name,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))
        if (imageId != 0) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = sauce.name,
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
            sauce.ingredients.split(",").forEach { ingredient ->
                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = "• ${ingredient.trim()}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("⬅ Back")
        }
    }
}
