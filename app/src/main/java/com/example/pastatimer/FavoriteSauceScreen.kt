package com.example.pastatimer

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pastatimer.SauceEntity
import com.example.pastatimer.viewmodel.SauceViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


@Composable
fun FavoriteSauceScreen(
    navController: NavController,
    username: String,
    viewModel: SauceViewModel = viewModel()
) {
    val favoriteSauces by viewModel.favoriteSauces
    var showDialog by remember { mutableStateOf(false) }

    val allSauces = remember { mutableStateListOf<SauceEntity>() }

    // Încarcă toate sosurile (pentru lista din dialog)
    LaunchedEffect(Unit) {
        allSauces.clear()
        allSauces.addAll(viewModel.getAllSauces())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("❤️ Favorite Sauces", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { showDialog = true }) {
                Text("Add New")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (favoriteSauces.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(favoriteSauces) { sauce ->
                    SauceCardCompact(
                        sauce = sauce,
                        navController = navController,
                        onToggleFavorite = {
                            viewModel.toggleFavorite(sauce)
                        }
                    )
                }
            }
        }

        if (showDialog) {
            SauceSelectionDialog(
                allSauces = allSauces.filter { !it.isFavorite },
                onSelect = {
                    viewModel.toggleFavorite(it)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = { navController.navigate("home/$username") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text("⬅ Back to Menu")
    }

}

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
    )
}

fun getImageResourceId(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}