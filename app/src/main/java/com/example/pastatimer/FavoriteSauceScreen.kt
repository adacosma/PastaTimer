package com.example.pastatimer

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pastatimer.viewmodel.MainViewModel

/**
 * Composable screen that displays the user's favorite sauces
 * and allows adding new ones from the full list of sauces.
 */
@Composable
fun FavoriteSauceScreen(
    navController: NavController,
    username: String,
    viewModel: MainViewModel = viewModel()
) {
    // Observes LiveData for favorite sauces and all available sauces
    val favoriteSauces by viewModel.favoriteSauces.observeAsState(emptyList())
    val allSauces by viewModel.allSauces.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }

    // Load data when the screen is first shown
    LaunchedEffect(Unit) {
        viewModel.loadFavorites(username)
        viewModel.loadAllSauces()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "❤️ Favorite Sauces",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (favoriteSauces.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No favorite sauces yet.\nTap 'Add New' to choose one.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    items(favoriteSauces) { sauce ->
                        SauceCardCompact(
                            sauce = sauce,
                            navController = navController,
                            onToggleFavorite = {
                                viewModel.toggleFavorite(username, sauce)
                            }
                        )
                    }
                }
            }
        }

        // Butoane de jos
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("home/$username") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⬅ Back to Menu")
            }
        }

        // Dialog to select new favorite sauce
        if (showDialog) {
            SauceSelectionDialog(
                allSauces = allSauces.filter { sauce ->
                    favoriteSauces.none { it.id == sauce.id }
                },
                onSelect = {
                    viewModel.toggleFavorite(username, it)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

/**
 * Displays a compact card with sauce image, name, and a favorite icon.
 * Clicking on the card navigates to the sauce details screen.
 */
@Composable
fun SauceCardCompact(
    sauce: SauceEntity,
    navController: NavController,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val imageId = remember(sauce.imageResName) {
        getImageResourceId(context, sauce.imageResName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                navController.navigate("details/${sauce.name}")
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageId != 0) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = sauce.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🍅", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(sauce.name, style = MaterialTheme.typography.titleMedium)

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Unfavorite"
                )
            }
        }
    }
}

/**
 * A dialog that lists available sauces for the user to add to favorites.
 */
@Composable
fun SauceSelectionDialog(
    allSauces: List<SauceEntity>,
    onSelect: (SauceEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Select a Sauce") },
        text = {
            Column {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(allSauces) { sauce ->
                        Text(
                            text = sauce.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(sauce) }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    )
}

/**
 * Utility function that retrieves the drawable resource ID for a sauce image.
 */
fun getImageResourceId(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}
