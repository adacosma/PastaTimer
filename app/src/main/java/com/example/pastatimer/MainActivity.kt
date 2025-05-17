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
        val sauceDao = db.sauceDao()
        if (sauceDao.getAll().isEmpty()) {
            sauceDao.insertAll(defaultSauceList)
        }
        val allSauces = sauceDao.getAll()
        setContent {
            PastaTimerTheme {
                NavGraph()

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

val defaultSauceList = listOf(
    SauceEntity(name = "Carbonara", ingredients = "eggs, pancetta, parmesan cheese, black pepper",
                imageResName = "carbonara"),
    SauceEntity(name = "Marinara", ingredients = "tomatoes, garlic, onion, basil, olive oil",
                imageResName = "marinara"),
    SauceEntity(name = "Pesto", ingredients = "basil, pine nuts, garlic, parmesan cheese, olive oil",
                imageResName = "pesto"),
    SauceEntity(name = "Alfredo", ingredients = "butter, heavy cream, parmesan cheese, garlic",
                imageResName = "alfredo"),
    SauceEntity(name = "Bolognese", ingredients = "ground beef, tomatoes, onion, garlic, carrots, celery",
                imageResName = "bolognese"),
    SauceEntity(name = "Arrabbiata", ingredients = "tomatoes, garlic, chili flakes, olive oil",
            imageResName = "arrabbiata"),
    SauceEntity(name = "Gorgonzola", ingredients = "gorgonzola cheese, cream, butter, garlic",
                imageResName = "gorgonzola"),
    SauceEntity(name = "Mushroom", ingredients = "mushrooms, cream, garlic, butter, onion",
                imageResName = "mushroom"),
    SauceEntity(name = "Vodka Sauce", ingredients = "vodka, tomatoes, cream, onion, garlic",
                imageResName = "vodka"),
    SauceEntity(name = "Cheese Sauce", ingredients = "cheddar cheese, milk, butter, flour",
                imageResName = "cheese_sauce"),
    SauceEntity(name = "Romesco", ingredients = "roasted red peppers, almonds, tomatoes, garlic, olive oil",
                imageResName = "romesco"),
    SauceEntity(name = "Puttanesca", ingredients = "tomatoes, olives, capers, anchovies, garlic",
                imageResName = "puttanesca")
)


@Preview(showBackground = true)
@Composable
fun PastaCountPreview() {
    PastaTimerTheme {
        Text("Pasta Number: ${defaultPastaList.size}")
    }
                NavGraph();
            }


