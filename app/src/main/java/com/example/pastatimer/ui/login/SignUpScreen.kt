package com.example.pastatimer.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import com.example.pastatimer.AppDatabase
import com.example.pastatimer.UserEntity

/**
 * Composable screen that handles the **user registration** (sign up) process.
 *
 * - Collects username and password input from the user.
 * - Validates input (non-empty username, matching passwords).
 * - Interacts with the local Room database to create a new account if the username is unique.
 * - Shows feedback via Snackbars and navigates to the login screen after success.
 *
 */

/**
 * Composable function that displays the Sign-Up screen.
 *
 * Allows users to register a new account by providing a username and matching passwords.
 * Verifies input, checks for username uniqueness using Room database,
 * and navigates back to the login screen upon successful registration.
 *
 * @param navController The navigation controller used to navigate between screens.
 */
@Composable
fun SignUpScreen(navController: NavController) {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create new account",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("User") },
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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                if (user.isBlank()) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Username cannot be empty",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else if (password != confirmPassword || password.isBlank()) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Passwords do not match",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    coroutineScope.launch {
                        val existingUser = userDao.getUserByUsername(user)
                        if (existingUser == null) {
                            val newUser = UserEntity(
                                username = user,
                                password = password,
                                isVegetarian = false,
                                allergens = ""
                            )
                            userDao.insertUser(newUser)
                            snackbarHostState.showSnackbar(
                                message = "Account created successfully",
                                duration = SnackbarDuration.Short
                            )
                            delay(300)
                            navController.navigate("login")
                        } else {
                            snackbarHostState.showSnackbar(
                                message = "Username already exists",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Sign Up")
        }

        MySnackbar(snackbarHostState = snackbarHostState)
    }
}
