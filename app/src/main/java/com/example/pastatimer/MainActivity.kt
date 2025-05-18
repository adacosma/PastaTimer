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

        val sauceDao = db.sauceDao()
        if (sauceDao.getAll().isEmpty()) {
            sauceDao.insertAll(defaultSauceList)
        }

        setContent {
            PastaTimerTheme {
                NavGraph()

            }
        }
    }
}

val defaultPastaList = listOf(
    PastaTypeEntity(name = "Spaghetti", boilTime = 8, imageResName = "spaghetti", flourType = "durum wheat"),
    PastaTypeEntity(name = "Penne", boilTime = 10, imageResName = "penne", flourType = "whole wheat"),
    PastaTypeEntity(name = "Fusilli", boilTime = 9, imageResName = "fusilli", flourType = "gluten-free"),
    PastaTypeEntity(name = "Macaroni", boilTime = 7, imageResName = "macaroni", flourType = "durum wheat"),
    PastaTypeEntity(name = "Farfalle", boilTime = 11, imageResName = "farfalle", flourType = "whole wheat"),
    PastaTypeEntity(name = "Fettuccine", boilTime = 9, imageResName =  "fettuccine", flourType = "durum wheat"),
    PastaTypeEntity(name = "Lasagna", boilTime = 12, imageResName = "lasagna", flourType =  "durum wheat"),
    PastaTypeEntity(name = "Gnocchi", boilTime = 6, imageResName = "gnocchi", flourType =  "potatoes"),
    PastaTypeEntity(name = "Oricchette", boilTime = 10, imageResName = "oricchette", flourType = "whole wheat"),
    PastaTypeEntity(name = "Orzo", boilTime = 8, imageResName = "orzo", flourType = "gluten-free"),
    PastaTypeEntity(name = "Pappardelle", boilTime = 11, imageResName = "pappardelle", flourType = "durum wheat"),
    PastaTypeEntity(name = "Cannelloni", boilTime = 13, imageResName =  "cannelloni", flourType = "whole wheat") )

val defaultSauceList = listOf(
    SauceEntity(name = "Carbonara", ingredients = "eggs, guanciale, parmesan cheese, pecorino, salt, black pepper",
                imageResName = "carbonara"),
    SauceEntity(name = "Marinara", ingredients = "tomatoes, garlic, onion, basil, olive oil, oregano, salt, pepper",
                imageResName = "marinara"),
    SauceEntity(name = "Pesto", ingredients = "basil, pine nuts, garlic, parmesan cheese, olive oil, lemon juice, salt, pepper",
                imageResName = "pesto"),
    SauceEntity(name = "Alfredo", ingredients = "butter, heavy cream, parmesan cheese, garlic, parsley, salt, pepper",
                imageResName = "alfredo"),
    SauceEntity(name = "Bolognese", ingredients = "ground beef, tomatoes, onion, garlic, carrots, celery, red wine, oregano, salt, pepper",
                imageResName = "bolognese"),
    SauceEntity(name = "Arrabbiata", ingredients = "tomatoes, garlic, chili flakes, basil, olive oil, salt, pepper",
            imageResName = "arrabbiata"),
    SauceEntity(name = "Gorgonzola", ingredients = "gorgonzola cheese, cream, butter, garlic, onion, salt, pepper",
                imageResName = "gorgonzola"),
    SauceEntity(name = "Mushroom", ingredients = "mushrooms, cream, garlic, butter, onion, parsley, salt, pepper",
                imageResName = "mushroom"),
    SauceEntity(name = "Cheese Sauce", ingredients = "cheddar cheese, parmesan cheese, gorgonzola, mozzarella, milk, butter, flour, salt, pepper",
                imageResName = "cheese_sauce"),
    SauceEntity(name = "Romesco", ingredients = "roasted red peppers, almonds, tomatoes, garlic, olive oil, vinegar, smoked paprika, salt, pepper",
                imageResName = "romesco"),
    SauceEntity(name = "Puttanesca", ingredients = "tomatoes, olives, capers, anchovies, garlic, basil, oregano, olive oil, salt, pepper",
                imageResName = "puttanesca"),
    SauceEntity(name = "Vodka Sauce", ingredients = "vodka, tomatoes, cream, onion, garlic, basil, salt, pepper",
        imageResName = "vodka")
)


@Preview(showBackground = true)
@Composable
fun PastaCountPreview() {

    NavGraph();
}


