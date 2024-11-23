package com.example.dishifyai.data

data class Recipe(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val servings: Int,
    val readyInMinutes: Int,
    val healthScore: Double,
    val spoonacularScore: Double,
    val pricePerServing: Double,
    val instructions: String,
    val ingredients: List<Ingredient>

)
