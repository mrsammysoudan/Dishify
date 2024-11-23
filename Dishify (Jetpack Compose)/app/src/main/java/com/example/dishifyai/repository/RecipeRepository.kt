package com.example.dishifyai.repository

import com.example.dishifyai.API
import com.example.dishifyai.data.*
import org.json.JSONArray
import org.json.JSONObject

class RecipeRepository(private val api: API = API()) {

    companion object {
        private val recipeCache = mutableMapOf<Int, Recipe>()
        private val ingredientsCache = mutableMapOf<Int, List<Ingredient>>()

        // Add a public getter for the ingredientsCache
        fun getIngredientsCache(): MutableMap<Int, List<Ingredient>> {
            return ingredientsCache
        }
    }
    suspend fun getRecipes(ingredientQuery: String): List<Recipe> {
        val jsonResponse = api.getRecipes(ingredientQuery) ?: return emptyList()
        return parseRecipes(jsonResponse)
    }

    suspend fun getRecipeDetails(recipeId: Int): String? {
        return api.getRecipeDetails(recipeId)
    }

    suspend fun getFilteredRecipes(
        diet: String,
        intolerance: String,
        minCalories: Int,
        maxCalories: Int
    ): List<Recipe> {
        val jsonResponse = api.getFilteredRecipes(diet, intolerance, minCalories, maxCalories)
            ?: return emptyList()
        return parseRecipes(jsonResponse)
    }

    suspend fun getBasicRecipeInfo(recipeId: Int): Recipe? {
        if (recipeCache.containsKey(recipeId)) {
            return recipeCache[recipeId]
        }
        val jsonResponse = api.getRecipeDetails(recipeId) ?: return null
        val jsonObject = JSONObject(jsonResponse)

        val ingredientsJsonArray = jsonObject.optJSONArray("extendedIngredients") ?: JSONArray()
        val ingredients = parseIngredients(ingredientsJsonArray)

        val recipe = Recipe(
            id = jsonObject.getInt("id"),
            title = jsonObject.getString("title"),
            imageUrl = jsonObject.getString("image"),
            servings = jsonObject.optInt("servings", 0),
            readyInMinutes = jsonObject.optInt("readyInMinutes", 0),
            healthScore = jsonObject.optDouble("healthScore", 0.0),
            spoonacularScore = jsonObject.optDouble("spoonacularScore", 0.0),
            pricePerServing = jsonObject.optDouble("pricePerServing", 0.0),
            instructions = jsonObject.optString("instructions", "No instructions provided"),
            ingredients = ingredients
        )
        recipeCache[recipeId] = recipe
        return recipe
    }

    suspend fun getIngredients(recipeId: Int): List<Ingredient> {
        if (ingredientsCache.containsKey(recipeId)) {
            return ingredientsCache[recipeId]!!
        }
        val jsonResponse = api.getIngredients(recipeId) ?: return emptyList()
        val jsonObject = JSONObject(jsonResponse)
        val ingredientsJsonArray = jsonObject.optJSONArray("ingredients") ?: JSONArray()
        val ingredients = parseIngredients(ingredientsJsonArray)
        ingredientsCache[recipeId] = ingredients
        return ingredients
    }

    private fun parseRecipes(jsonResponse: String): List<Recipe> {
        val jsonArray = JSONArray(jsonResponse)
        return List(jsonArray.length()) { i ->
            val recipeObject = jsonArray.getJSONObject(i)
            val nutritionArray = if (recipeObject.has("nutrition")) {
                recipeObject.getJSONObject("nutrition").getJSONArray("nutrients")
            } else {
                JSONArray()
            }

            val caloriesObject = (0 until nutritionArray.length())
                .map { nutritionArray.getJSONObject(it) }
                .find { it.getString("name") == "Calories" }

            Recipe(
                id = recipeObject.getInt("id"),
                title = recipeObject.getString("title"),
                imageUrl = recipeObject.getString("image"),
                servings = recipeObject.optInt("servings", 0),
                readyInMinutes = recipeObject.optInt("readyInMinutes", 0),
                healthScore = recipeObject.optDouble("healthScore", 0.0),
                spoonacularScore = recipeObject.optDouble("spoonacularScore", 0.0),
                pricePerServing = recipeObject.optDouble("pricePerServing", 0.0),
                instructions = recipeObject.optString("instructions", "No instructions provided"),
                ingredients = emptyList() // Replace with actual ingredients list if available
            )
        }
    }

    fun parseRecipeDetails(jsonResponse: String): RecipeDetails {
        val jsonObject = JSONObject(jsonResponse)
        val instructions = jsonObject.optString("instructions", "No instructions provided")
        return RecipeDetails(instructions)
    }

    fun parseIngredients(ingredientsArray: JSONArray): List<Ingredient> {
        return List(ingredientsArray.length()) { i ->
            val ingredientObject = ingredientsArray.getJSONObject(i)
            val amountObject = ingredientObject.optJSONObject("measures") ?: JSONObject()

            val metricObject = amountObject.optJSONObject("metric") ?: JSONObject()
            val metricAmount = Measurement(
                value = metricObject.optDouble("amount", 0.0),
                unit = metricObject.optString("unitShort", "")
            )

            val usObject = amountObject.optJSONObject("us") ?: JSONObject()
            val usAmount = Measurement(
                value = usObject.optDouble("amount", 0.0),
                unit = usObject.optString("unitShort", "")
            )

            Ingredient(
                name = ingredientObject.optString("name", ""),
                image = ingredientObject.optString("image", ""),
                amount = Amount(metric = metricAmount, us = usAmount)
            )
        }
    }
}
