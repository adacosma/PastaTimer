package com.example.pastatimer.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pastatimer.AppDatabase
import kotlinx.coroutines.launch


@Composable
fun LogInScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = "Log In",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("user") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    val user = userDao.getUserByUsername(username)
                    if (user == null) {
                        snackbarHostState.showSnackbar(
                            message = "Username not found",
                            duration = SnackbarDuration.Short
                        )
                    } else if (user.password != password) {
                        snackbarHostState.showSnackbar(
                            message = "Incorrect password",
                            duration = SnackbarDuration.Short
                        )
                    } else {
                        navController.navigate("home")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Log In")
        }

        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account?")
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = { navController.navigate("sign up") }) {
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        MySnackbar(snackbarHostState = snackbarHostState)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    com.example.pastatimer.ui.theme.PastaTimerTheme {
        LogInScreen(navController = rememberNavController())
    }
}