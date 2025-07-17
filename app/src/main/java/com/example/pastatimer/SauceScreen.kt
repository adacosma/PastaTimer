package com.example.pastatimer

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
import androidx.compose.runtime.livedata.observeAsState
import com.example.pastatimer.viewmodel.MainViewModel

/**
 * Composable screen that displays the list of sauces filtered by user preferences (allergens, vegetarian).
 *
 * MVVM Architecture:
 * - Uses LiveData from MainViewModel to observe the list of sauces.
 * - Data is automatically loaded and filtered based on the logged-in user's preferences.
 * - Supports pagination and navigation to sauce details screen.
 *
 * @param navController Used for screen navigation.
 * @param username The logged-in user's username.
 * @param viewModel The shared MainViewModel instance that provides data.
 */
@Composable
fun SauceScreen(
    navController: NavController,
    username: String,
    viewModel: MainViewModel
) {

    val filteredSauces by viewModel.filteredSauces.observeAsState(emptyList())

    LaunchedEffect(username) {
        viewModel.loadUserAndSauces(username)
    }


    // Paginare
    var pageIndex by remember { mutableIntStateOf(0) }
    val itemsPerPage = 6
    val pageCount = (filteredSauces.size + itemsPerPage - 1) / itemsPerPage
    val currentItems = filteredSauces.drop(pageIndex * itemsPerPage).take(itemsPerPage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Text("\uD83C\uDF45 Pasta Sauces:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (filteredSauces.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No sauces match your preferences.",
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
                    .padding(horizontal = 4.dp)
            ) {
                items(currentItems) { sauce ->
                    SauceCard(sauce = sauce, navController = navController)
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
            onClick = { navController.navigate("home/${username}") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("⬅ Back to Menu")
        }
    }
}

@Composable
fun SauceCard(sauce: SauceEntity, navController: NavController) {
    val context = LocalContext.current
    val imageId = remember(sauce.imageResName) {
        context.resources.getIdentifier(sauce.imageResName, "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
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
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("\uD83C\uDF45", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(sauce.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navController.navigate("details/${sauce.name}")
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("See Ingredients for ${sauce.name}")
            }
        }
    }
}