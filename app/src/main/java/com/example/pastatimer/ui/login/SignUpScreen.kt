package com.example.pastatimer.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pastatimer.viewmodel.MainViewModel
import com.example.pastatimer.viewmodel.AuthResult
import kotlinx.coroutines.delay

/**
 * Composable screen that handles user registration, following the MVVM architecture.
 *
 * The UI reacts to registration results via LiveData exposed by the ViewModel,
 * offering real-time validation, error messages, and navigation.
 *
 * @param navController Used for navigating to the login screen after success.
 * @param viewModel Shared ViewModel that handles sign-up logic and state.
 */
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Observe registration result from ViewModel (LiveData)
    val signUpResult by viewModel.authSignUpResult.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    /**
     * Effect that listens to ViewModel result changes and reacts accordingly:
     * - On success: shows confirmation and navigates to login screen.
     * - On error: shows error message from ViewModel.
     * - On loading: disables form and shows progress spinner.
     */
    LaunchedEffect(signUpResult) {
        signUpResult?.let { result ->
            when (result) {
                is AuthResult.Success -> {
                    snackbarHostState.showSnackbar(
                        message = "Account created successfully!",
                        duration = SnackbarDuration.Short
                    )
                    delay(300)
                    navController.navigate("login") {
                        popUpTo("sign up") { inclusive = true }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create New Account",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = signUpResult !is AuthResult.Loading
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation(),
            enabled = signUpResult !is AuthResult.Loading
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = signUpResult !is AuthResult.Loading,
            isError = confirmPassword.isNotEmpty() && password != confirmPassword
        )

        // Password strength indicator
        if (password.isNotEmpty()) {
            Text(
                text = when {
                    password.length < 4 -> "Password too short (min 4 characters)"
                    password.length < 6 -> "Password strength: Weak"
                    password.length < 8 -> "Password strength: Medium"
                    else -> "Password strength: Strong"
                },
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    password.length < 4 -> MaterialTheme.colorScheme.error
                    password.length < 6 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Button(
            onClick = {
                viewModel.signUp(user, password, confirmPassword)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            enabled = signUpResult !is AuthResult.Loading
        ) {
            // Loading indicator
            if (signUpResult is AuthResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Sign Up")
        }

        // Link to login
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account?")
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("sign up") { inclusive = true }
                    }
                },
                enabled = signUpResult !is AuthResult.Loading
            ) {
                Text(
                    text = "Log In",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        MySnackbar(snackbarHostState = snackbarHostState)
    }
}