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

@Composable
fun SauceScreen(sauces: List<SauceEntity>, navController: NavController, username: String) {
    var pageIndex by remember { mutableIntStateOf(0) }
    val itemsPerPage = 6
    val pageCount = (sauces.size + itemsPerPage - 1) / itemsPerPage
    val currentItems = sauces.drop(pageIndex * itemsPerPage).take(itemsPerPage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Text("🍅 Pasta Sauces:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (sauces.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No sauces found in the database\n",
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
            onClick = { navController.navigate("home/$username") },
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
            .padding(4.dp)
            ,
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
                    Text("🍅", style = MaterialTheme.typography.headlineLarge)
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
