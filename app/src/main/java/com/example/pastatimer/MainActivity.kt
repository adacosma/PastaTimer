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
 * MainActivity is the entry point of the PastaTimer application.
 *
 * It uses Jetpack Compose to build the UI and applies the custom PastaTimerTheme.
 * The main navigation flow is handled through NavGraph().
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



/**
 * Preview function for design tools (Android Studio)
 * Allows a developer to see the NavGraph in preview mode.
 */
@Preview(showBackground = true)
@Composable
fun PastaCountPreview() {

    NavGraph();
}


