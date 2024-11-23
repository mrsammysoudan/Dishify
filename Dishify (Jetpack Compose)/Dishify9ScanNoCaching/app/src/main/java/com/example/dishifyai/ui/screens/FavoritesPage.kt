package com.example.dishifyai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dishifyai.data.*

class FavoritesPage {
    @Composable
    fun FavoritesScreen(navController: NavHostController) {
        val favoriteRecipes = listOf(
            Recipe(
                id = 1,
                title = "Spaghetti Carbonara",
                imageUrl = "https://example.com/spaghetti.jpg",
                servings = 4,
                readyInMinutes = 30,
                healthScore = 80.0,
                spoonacularScore = 90.0,
                pricePerServing = 1.5,
                instructions = "Cook pasta, mix with eggs and cheese, add bacon.",
                ingredients = listOf(
                    Ingredient("Spaghetti", "https://spoonacular.com/cdn/ingredients_100x100/spaghetti.jpg", Amount(Measurement(200.0, "grams"), Measurement(7.05, "oz"))),
                    Ingredient("Bacon", "https://spoonacular.com/cdn/ingredients_100x100/bacon.jpg", Amount(Measurement(100.0, "grams"), Measurement(3.5, "oz")))
                )
            ),
            Recipe(
                id = 2,
                title = "Chicken Alfredo",
                imageUrl = "https://example.com/chicken.jpg",
                servings = 4,
                readyInMinutes = 40,
                healthScore = 75.0,
                spoonacularScore = 85.0,
                pricePerServing = 2.0,
                instructions = "Cook chicken, prepare Alfredo sauce, mix with pasta.",
                ingredients = listOf(
                    Ingredient("Chicken", "https://spoonacular.com/cdn/ingredients_100x100/chicken.jpg", Amount(Measurement(300.0, "grams"), Measurement(10.5, "oz"))),
                    Ingredient("Alfredo Sauce", "https://spoonacular.com/cdn/ingredients_100x100/alfredo-sauce.jpg", Amount(Measurement(150.0, "grams"), Measurement(5.25, "oz")))
                )
            ),
            Recipe(
                id = 3,
                title = "Beef Stroganoff",
                imageUrl = "https://example.com/beef.jpg",
                servings = 4,
                readyInMinutes = 50,
                healthScore = 70.0,
                spoonacularScore = 80.0,
                pricePerServing = 2.5,
                instructions = "Cook beef, prepare Stroganoff sauce, serve with noodles.",
                ingredients = listOf(
                    Ingredient("Beef", "https://spoonacular.com/cdn/ingredients_100x100/beef.jpg", Amount(Measurement(400.0, "grams"), Measurement(14.1, "oz"))),
                    Ingredient("Stroganoff Sauce", "https://spoonacular.com/cdn/ingredients_100x100/sauce.jpg", Amount(Measurement(200.0, "grams"), Measurement(7.05, "oz")))
                )
            )
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Favorite Recipes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    items(favoriteRecipes) { recipe ->
                        FavoriteRecipeCard(recipe)
                    }
                }
            }
            // Bottom Navigation Bar
            BottomNavigationBar(navController, Modifier.align(Alignment.BottomCenter))
        }
    }

    @Composable
    fun FavoriteRecipeCard(recipe: Recipe) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val painter = rememberAsyncImagePainter(model = recipe.imageUrl)
                Image(
                    painter = painter,
                    contentDescription = "${recipe.title} image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp), // Ensures the image has a minimum height
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = recipe.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
        NavigationBar(modifier = modifier) {
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = false, // Set selected state based on your logic
                onClick = { /* Handle navigation */ }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
                label = { Text("Favorites") },
                selected = true, // Set selected state based on your logic
                onClick = { /* Handle navigation */ }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                label = { Text("Profile") },
                selected = false, // Set selected state based on your logic
                onClick = { /* Handle navigation */ }
            )
        }
    }
}
