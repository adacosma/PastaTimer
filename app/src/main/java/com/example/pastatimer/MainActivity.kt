package com.example.pastatimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pastatimer.ui.screens.PastaScreen
import com.example.pastatimer.ui.theme.PastaTimerTheme
import com.example.pastatimer.PastaTypeEntity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val dao = db.pastaTypeDao()
        if (dao.getAll().isEmpty()) {
            dao.insertAll(defaultPastaList)
        }

        val allPastas = dao.getAll()

        setContent {
            PastaTimerTheme {
                PastaScreen(pastas = allPastas)
            }
        }
    }
}

val defaultPastaList = listOf(
    PastaTypeEntity(name = "Spaghetti", boilTime = 8, imageResName = "spaghetti", flourType = "grâu dur"),
    PastaTypeEntity(name = "Penne", boilTime = 10, imageResName = "penne", flourType = "integrală"),
    PastaTypeEntity(name = "Fusilli", boilTime = 9, imageResName = "fusilli", flourType = "fără gluten"),
    PastaTypeEntity(name = "Macaroni", boilTime = 7, imageResName = "macaroni", flourType = "grâu dur"),
    PastaTypeEntity(name = "Farfalle", boilTime = 11, imageResName = "farfalle", flourType = "integrală"),
    PastaTypeEntity(name = "Fettuccine", boilTime = 9, imageResName =  "fettuccine", flourType = "ou + grâu dur"),
    PastaTypeEntity(name = "Lasagna", boilTime = 12, imageResName = "lasagna", flourType =  "grâu dur"),
    PastaTypeEntity(name = "Gnocchi", boilTime = 6, imageResName = "gnocchi", flourType =  "cartofi"),
    PastaTypeEntity(name = "Oricchette", boilTime = 10, imageResName = "oricchette", flourType = "integrală"),
    PastaTypeEntity(name = "Orzo", boilTime = 8, imageResName = "orzo", flourType = "grâu dur"),
    PastaTypeEntity(name = "Pappardelle", boilTime = 11, imageResName = "pappardelle", flourType = "ou + grâu dur"),
    PastaTypeEntity(name = "Cannelloni", boilTime = 13, imageResName =  "cannelloni", flourType = "grâu dur") )

@Preview(showBackground = true)
@Composable
fun PastaCountPreview() {
    PastaTimerTheme {
        Text("Pasta Number: ${defaultPastaList.size}")
    }
}