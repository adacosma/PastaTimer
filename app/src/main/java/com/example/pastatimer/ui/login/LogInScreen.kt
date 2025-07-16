package com.example.pastatimer.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pastatimer.viewmodel.MainViewModel
import com.example.pastatimer.viewmodel.AuthResult

/**
 * Composable function that displays the Log In screen following MVVM pattern.
 *
 * Uses MainViewModel to handle authentication logic, keeps UI clean,
 * and observes authentication results through LiveData.
 *
 * @param navController The navigation controller used for route navigation.
 * @param viewModel The MainViewModel handling authentication logic.
 */

@Composable
fun LogInScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observă rezultatele din ViewModel în loc să accesezi direct DB
    val loginResult by viewModel.authLoginResult.observeAsState()
    val currentUser by viewModel.user.observeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            when (result) {
                is AuthResult.Success -> {
                    currentUser?.let { user ->
                        navController.navigate("home/${user.username}") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                is AuthResult.Error -> {
                    snackbarHostState.showSnackbar(
                        message = result.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is AuthResult.Loading -> {
                    // Loading state
                }
            }
        }
    }

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
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = loginResult !is AuthResult.Loading
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation(),
            enabled = loginResult !is AuthResult.Loading
        )

        Button(
            onClick = {
                // Simplu apel la ViewModel
                viewModel.login(username, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            enabled = loginResult !is AuthResult.Loading
        ) {
            // Loading indicator
            if (loginResult is AuthResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Log In")
        }

        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account?")
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = { navController.navigate("sign up") },
                enabled = loginResult !is AuthResult.Loading
            ) {
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        MySnackbar(snackbarHostState = snackbarHostState)
    }
}