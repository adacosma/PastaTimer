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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pastatimer.viewmodel.MainViewModel
import com.example.pastatimer.PastaTypeEntity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState

/**
 * Composable screen that displays a paginated list of all pasta types available in the database.
 *
 * Follows MVVM architecture:
 * - Gets pasta data from the MainViewModel via LiveData
 * - Displays each pasta type in a card with image and details
 * - Allows navigation to a timer screen for the selected pasta
 *
 * @param navController The navigation controller used for routing.
 * @param username The current logged-in user's username.
 * @param viewModel Shared MainViewModel to fetch and observe pasta data.
 */
@Composable
fun PastaScreen(
    navController: NavController,
    username: String,
    viewModel: MainViewModel
) {
    val pastaTypes by viewModel.pastaTypes.observeAsState(emptyList())

    var pageIndex by remember { mutableIntStateOf(0) }
    val itemsPerPage = 6
    val pageCount = (pastaTypes.size + itemsPerPage - 1) / itemsPerPage
    val currentItems = pastaTypes.drop(pageIndex * itemsPerPage).take(itemsPerPage)

    LaunchedEffect(Unit) {
        viewModel.loadPastaTypes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "\uD83C\uDF5D List of pasta types (${pastaTypes.size})",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (pastaTypes.isEmpty()) {
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
                    PastaCard(pasta = pasta, navController = navController, username = username)
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
                        Text("\u25C0 Previous")
                    }

                    Text(
                        "Page ${pageIndex + 1} of $pageCount",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Button(
                        onClick = { if (pageIndex < pageCount - 1) pageIndex++ },
                        enabled = pageIndex < pageCount - 1
                    ) {
                        Text("Next \u25B6")
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("home/$username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("\u2B05 Back to Menu")
        }
    }
}

/**
 * Composable component that represents a single pasta card in the list.
 *
 * Displays the image, name, boil time, and flour type.
 * Provides a button to start the timer for cooking the pasta.
 *
 * @param pasta The pasta entity to display.
 * @param navController For navigating to the timer screen.
 * @param username The current logged-in user.
 */
@Composable
fun PastaCard(pasta: PastaTypeEntity, navController: NavController, username: String) {
    val context = LocalContext.current
    val imageId = remember(pasta.imageResName) {
        context.resources.getIdentifier(
            pasta.imageResName,
            "drawable",
            context.packageName
        )
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