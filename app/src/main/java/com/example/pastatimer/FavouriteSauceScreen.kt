//package com.example.pastatimer
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//
//@Composable
//fun FavoriteSauceScreen(
//    navController: NavController,
//    allSauces: List<SauceEntity>
//) {
//    var isSearchActive by remember { mutableStateOf(false) }
//    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
//    val favoriteList = remember { mutableStateListOf<SauceEntity>() }
//
//    val filtered = allSauces.filter {
//        it.name.contains(searchQuery.text, ignoreCase = true)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if (isSearchActive) {
//                OutlinedTextField(
//                    value = searchQuery,
//                    onValueChange = { searchQuery = it },
//                    label = { Text("Search sauces") },
//                    modifier = Modifier.weight(1f)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Button(onClick = { isSearchActive = false }) {
//                    Text("Cancel")
//                }
//            } else {
//                Button(onClick = {
//                    isSearchActive = true
//                    searchQuery = TextFieldValue("")
//                }) {
//                    Text("Add New")
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isSearchActive) {
//            LazyColumn {
//                items(filtered) { sauce ->
//                    SauceSearchItem(sauce = sauce) {
//                        if (!favoriteList.any { it.id == sauce.id }) {
//                            favoriteList.add(sauce)
//                        }
//                        isSearchActive = false
//                    }
//                }
//            }
//        } else {
//            if (favoriteList.isEmpty()) {
//                Text("No favorites added yet.", style = MaterialTheme.typography.bodyLarge)
//            } else {
//                LazyColumn {
//                    items(favoriteList, key = { it.id }) { sauce ->
//                        SauceCardFavoriteStyle(sauce = sauce, navController = navController)
//                    }
//                }
//
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedButton(onClick = { navController.popBackStack() }) {
//            Text("⬅ Back")
//        }
//    }
//}
//
//@Composable
//fun SauceSearchItem(sauce: SauceEntity, onAdd: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(12.dp)
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(sauce.name)
//            Button(onClick = onAdd) {
//                Text("Add")
//            }
//        }
//    }
//}
//
//@Composable
//fun SauceCardFavoriteStyle(sauce: SauceEntity, navController: NavController) {
//    val context = LocalContext.current
//    val imageId = remember(sauce.imageResName) {
//        context.resources.getIdentifier(sauce.imageResName, "drawable", context.packageName)
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(12.dp)
//                .fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            if (imageId != 0) {
//                Image(
//                    painter = painterResource(id = imageId),
//                    contentDescription = sauce.name,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(100.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(sauce.name, style = MaterialTheme.typography.titleMedium)
//
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedButton(
//                onClick = {
//                    navController.navigate("details/${sauce.name}")
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("See Ingredients")
//            }
//        }
//    }
//}
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FavoriteSauceScreen(
    navController: NavController,
    allSauces: List<SauceEntity>
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Folosim remember pentru a păstra lista de favorite între recompuneri
    val favoriteList = remember { mutableStateListOf<SauceEntity>() }

    val filtered = allSauces.filter {
        it.name.contains(searchQuery.text, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🌟 Favorite Sauces:", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = {
                isSearchActive = true
                searchQuery = TextFieldValue("")
            }) {
                Text("Add New")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isSearchActive) {
            // Afișăm căutarea și rezultatele
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search sauces") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { isSearchActive = false },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filtered.isEmpty()) {
                Text(
                    "No sauces found matching your search",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filtered) { sauce ->
                        SauceSearchCard(
                            sauce = sauce,
                            onAdd = {
                                // Verificăm dacă sosul există deja în lista de favorite
                                if (!favoriteList.any { it.id == sauce.id }) {
                                    favoriteList.add(sauce)
                                }
                                // Închide modul de căutare după adăugare
                                isSearchActive = false
                            }
                        )
                    }
                }
            }
        } else {
            // Afișăm sosurile favorite într-un grid similar cu SauceScreen
            if (favoriteList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No favorites added yet.\nPress 'Add New' to add your favorite sauces.",
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
                    items(favoriteList.toList(), key = { it.id }) { sauce ->
                        SauceCardFavoriteStyle(
                            sauce = sauce,
                            navController = navController,
                            onRemove = {
                                favoriteList.remove(sauce)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⬅ Back")
        }
    }
}

@Composable
fun SauceSearchCard(sauce: SauceEntity, onAdd: () -> Unit) {
    val context = LocalContext.current
    val imageId = remember(sauce.imageResName) {
        context.resources.getIdentifier(sauce.imageResName, "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
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
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Favorites")
            }
        }
    }
}

@Composable
fun SauceCardFavoriteStyle(
    sauce: SauceEntity,
    navController: NavController,
    onRemove: () -> Unit
) {
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
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🍅", style = MaterialTheme.typography.headlineLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(sauce.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRemove,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Remove")
                }

                Button(
                    onClick = {
                        navController.navigate("details/${sauce.name}")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Details")
                }
            }
        }
    }
}