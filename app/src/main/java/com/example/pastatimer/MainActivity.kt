package com.example.pastatimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pastatimer.ui.theme.PastaTimerTheme
import com.example.pastatimer.ui.login.LogInScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PastaTimerTheme {
                LogInScreen()
            }
        }
    }
}