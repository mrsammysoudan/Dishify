package com.example.dishifyai.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dishifyai.data.Ingredient
import com.example.dishifyai.data.Recipe
import com.example.dishifyai.data.RecipeDetails
import com.example.dishifyai.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel(private val repository: RecipeRepository = RecipeRepository()) : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _ingredientsCache = RecipeRepository.getIngredientsCache()

    fun getRecipeById(recipeId: String?): Recipe? {
        return recipes.value.firstOrNull { it.id.toString() == recipeId }
    }

    fun getBasicRecipeInfo(recipeId: Int): LiveData<Recipe?> {
        val basicRecipeInfo = MutableLiveData<Recipe?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val recipe = repository.getBasicRecipeInfo(recipeId)
                Log.d("RecipeViewModel", "Fetched basic recipe info: $recipe")
                basicRecipeInfo.postValue(recipe)
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching basic recipe info", e)
                basicRecipeInfo.postValue(null)
            }
        }
        return basicRecipeInfo
    }

    private val _minCalories = MutableStateFlow(0)
    val minCalories: StateFlow<Int> = _minCalories

    private val _maxCalories = MutableStateFlow(2000)
    val maxCalories: StateFlow<Int> = _maxCalories

    val selectedDietaryOption = MutableStateFlow("None")
    val selectedIntoleranceOption = MutableStateFlow("None")

    fun setSelectedDietaryOption(option: String) {
        selectedDietaryOption.value = option
        updateRecipes()
    }

    fun setSelectedIntoleranceOption(option: String) {
        selectedIntoleranceOption.value = option
        updateRecipes()
    }

    private val _scannedItems = MutableStateFlow<List<String>>(emptyList())
    val scannedItems: StateFlow<List<String>> = _scannedItems

    fun addScannedItem(item: String) {
        _scannedItems.value = _scannedItems.value + item
    }

    fun updateRecipes() {
        viewModelScope.launch {
            fetchFilteredRecipes(
                selectedDietaryOption.value,
                selectedIntoleranceOption.value,
                minCalories.value,
                maxCalories.value
            )
        }
    }

    fun getIngredients(recipeId: Int): LiveData<List<Ingredient>> {
        val ingredientsLiveData = MutableLiveData<List<Ingredient>>()

        _ingredientsCache[recipeId]?.let {
            ingredientsLiveData.postValue(it)
            return ingredientsLiveData
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ingredients = repository.getIngredients(recipeId)
                Log.d("RecipeViewModel", "Fetched ingredients from repository: $ingredients")
                _ingredientsCache[recipeId] = ingredients
                ingredientsLiveData.postValue(ingredients)
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching ingredients", e)
                ingredientsLiveData.postValue(emptyList())
            }
        }
        return ingredientsLiveData
    }

    fun fetchFilteredRecipes(diet: String, intolerance: String, minCalories: Int, maxCalories: Int) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedRecipes = repository.getFilteredRecipes(diet, intolerance, minCalories, maxCalories)
                withContext(Dispatchers.Main) {
                    _recipes.value = fetchedRecipes
                    Log.d("RecipeViewModel", "Fetched recipes: ${fetchedRecipes.joinToString { it.title }}")
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching recipes", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchRecipes(ingredientQuery: String) {
        Log.d("RecipeViewModel", "Starting to fetch recipes for: $ingredientQuery")
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedRecipes = repository.getRecipes(ingredientQuery)
                Log.d("RecipeViewModel", "Fetched recipes: ${fetchedRecipes.joinToString { it.title }}")
                withContext(Dispatchers.Main) {
                    _recipes.value = fetchedRecipes
                    Log.d("RecipeViewModel", "Recipes state updated with new recipes")
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching recipes", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchRecipeDetails(recipeId: Int): LiveData<RecipeDetails?> {
        val recipeDetails = MutableLiveData<RecipeDetails?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val details = repository.getRecipeDetails(recipeId)
                Log.d("RecipeViewModel", "Fetched recipe details: $details")
                details?.let {
                    recipeDetails.postValue(repository.parseRecipeDetails(it))
                } ?: run {
                    Log.e("RecipeViewModel", "Recipe details response is null")
                    recipeDetails.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching recipe details", e)
                recipeDetails.postValue(null)
            }
        }
        return recipeDetails
    }

    // Adding the fetchRecipeResults function back
    fun fetchRecipeResults(ingredientQuery: String) {
        Log.d("RecipeViewModel", "Starting to fetch recipes for: $ingredientQuery")
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val recipeResults = repository.getRecipes(ingredientQuery)
                Log.d("RecipeViewModel", "Fetched recipes: ${recipeResults.joinToString { it.title }}")
                withContext(Dispatchers.Main) {
                    _recipes.value = recipeResults
                    Log.d("RecipeViewModel", "Recipes state updated with new recipes")
                }
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching recipe results", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
