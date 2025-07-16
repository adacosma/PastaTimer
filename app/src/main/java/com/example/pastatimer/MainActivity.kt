package com.example.pastatimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pastatimer.navigation.NavGraph
import com.example.pastatimer.ui.theme.PastaTimerTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pastatimer.ui.screens.PastaScreen
import com.example.pastatimer.ui.theme.PastaTimerTheme
import com.example.pastatimer.PastaTypeEntity
import com.example.pastatimer.ui.login.LogInScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Main entry point of the PastaTimer application.
 *
 * Initializes the Room database with default pasta and sauce data (if empty),
 * and sets up the Compose UI using [NavGraph] and [PastaTimerTheme].
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            PastaTimerTheme {
                NavGraph()
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PastaCountPreview() {

    NavGraph();
}


