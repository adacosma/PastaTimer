package com.example.pastatimer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pastatimer.PastaTypeEntity
import android.util.Log
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.pastatimer.UserEntity

/**
 * Composable screen that displays a paginated grid of available pasta types.
 *
 * @param pastas List of pasta types from the database.
 * @param user The current logged-in user, used to navigate with username.
 * @param navController Navigation controller for routing.
 */
@Composable
fun PastaScreen(pastas: List<PastaTypeEntity>, user: UserEntity,
                navController: NavController) {
    var pageIndex by remember { mutableIntStateOf(0) }
    val itemsPerPage = 6
    val pageCount = (pastas.size + itemsPerPage - 1) / itemsPerPage
    val currentItems = pastas.drop(pageIndex * itemsPerPage).take(itemsPerPage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "🍝 List of pasta types (${pastas.size})",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (pastas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No pasta found in database",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(currentItems) { pasta ->
                    PastaCard(pasta = pasta, navController = navController, username = user.username)
                }
            }

            if (pageCount > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { if (pageIndex > 0) pageIndex-- },
                        enabled = pageIndex > 0
                    ) {
                        Text("◀ Previous")
                    }

                    Text(
                        "Page ${pageIndex + 1} of $pageCount",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Button(
                        onClick = { if (pageIndex < pageCount - 1) pageIndex++ },
                        enabled = pageIndex < pageCount - 1
                    ) {
                        Text("Next ▶")
                    }
                }
            }
        }
        Button(
            onClick = { navController.navigate("home/${user.username}")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("⬅ Back to Menu")
        }
    }
}

/**
 * Composable that displays a single pasta item in a card layout.
 *
 * Includes pasta image, name, boil time, and flour type.
 * A button is provided to start the cooking timer for the selected pasta.
 *
 * @param pasta The pasta entity to display.
 * @param navController Navigation controller used to go to the timer screen.
 * @param username The current user's username (for route building).
 */
@Composable
fun PastaCard(pasta: PastaTypeEntity, navController: NavController, username: String){
    val context = LocalContext.current
    val imageId = remember(pasta.imageResName) {
        try {
            val resId = context.resources.getIdentifier(
                pasta.imageResName,
                "drawable",
                context.packageName
            )
            if (resId == 0) {
                Log.w("PastaScreen", "No image found: ${pasta.imageResName}")
            }
            resId
        } catch (e: Exception) {
            Log.e("PastaScreen", "Error while loading ${pasta.imageResName}: ${e.message}")
            0
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            if (imageId != 0) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = pasta.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(pasta.name, style = MaterialTheme.typography.titleMedium)
            Text("Time: ${pasta.boilTime} min", style = MaterialTheme.typography.bodySmall)
            Text("Flour: ${pasta.flourType}", style = MaterialTheme.typography.bodySmall)

            Button(
                onClick = {
                    navController.navigate("timer/${pasta.name}/${pasta.boilTime}/$username")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Start Timer for ${pasta.name}")
            }
        }
    }
}
