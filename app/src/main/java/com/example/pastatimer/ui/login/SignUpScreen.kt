package com.example.pastatimer.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import com.example.pastatimer.AppDatabase
import com.example.pastatimer.UserEntity


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
                if (password == confirmPassword && password.isNotBlank()) {
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
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Passwords do not match",
                            duration = SnackbarDuration.Short
                        )
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

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    com.example.pastatimer.ui.theme.PastaTimerTheme {
        SignUpScreen(navController = rememberNavController())
    }
}
