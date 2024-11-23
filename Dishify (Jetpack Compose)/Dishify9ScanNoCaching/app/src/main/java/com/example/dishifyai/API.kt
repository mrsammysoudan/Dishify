package com.example.dishifyai

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class API(private val client: OkHttpClient = createCustomClient()) {

    private val cacheExpirationTimeMillis = TimeUnit.MINUTES.toMillis(10) // Cache expiration time

    private val recipeDetailsCache = ConcurrentHashMap<Int, CachedResponse>()
    private val ingredientsCache = ConcurrentHashMap<Int, CachedResponse>()
    private val recipesCache = ConcurrentHashMap<String, CachedResponse>()
    private val filteredRecipesCache = ConcurrentHashMap<String, CachedResponse>()

    private val locks = ConcurrentHashMap<String, ReentrantLock>()
    private val inProgressRequests = ConcurrentHashMap<String, MutableSharedFlow<String?>>()

    companion object {
        fun createCustomClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Increase connect timeout
                .writeTimeout(30, TimeUnit.SECONDS)     // Increase write timeout
                .readTimeout(30, TimeUnit.SECONDS)      // Increase read timeout
                .build()
        }
    }

    private val baseUrl = "https://api.spoonacular.com/recipes"
    private val apiKey = "0a6354dbaba643d8a3e010e9c6bdf60a"  // Replace with your actual API keys

    private fun getLock(key: String): ReentrantLock {
        return locks.computeIfAbsent(key) { ReentrantLock() }
    }

    suspend fun getRecipeDetails(recipeId: Int): String? {
        val cacheKey = "recipeDetails_$recipeId"
        return getOrFetchData(
            cacheKey,
            { recipeDetailsCache[recipeId]?.takeIf { !isCacheExpired(it.timestamp) }?.response },
            {
                val url = "$baseUrl/$recipeId/information?includeNutrition=false&apiKey=$apiKey"
                makeRequest(url)?.also {
                    recipeDetailsCache[recipeId] = CachedResponse(it)
                }
            }
        )
    }

    suspend fun getIngredients(recipeId: Int): String? {
        val cacheKey = "ingredients_$recipeId"
        return getOrFetchData(
            cacheKey,
            { ingredientsCache[recipeId]?.takeIf { !isCacheExpired(it.timestamp) }?.response },
            {
                val url = "$baseUrl/$recipeId/ingredientWidget.json?apiKey=$apiKey"
                makeRequest(url)?.also {
                    ingredientsCache[recipeId] = CachedResponse(it)
                }
            }
        )
    }

    suspend fun getRecipes(ingredients: String): String? {
        val cacheKey = "recipes_$ingredients"
        return getOrFetchData(
            cacheKey,
            { recipesCache[ingredients]?.takeIf { !isCacheExpired(it.timestamp) }?.response },
            {
                val encodedIngredients = URLEncoder.encode(ingredients, "UTF-8")
                val url = "$baseUrl/findByIngredients?ingredients=$encodedIngredients&apiKey=$apiKey"
                makeRequest(url)?.also {
                    recipesCache[ingredients] = CachedResponse(it)
                }
            }
        )
    }

    suspend fun getFilteredRecipes(diet: String, intolerance: String, minCalories: Int, maxCalories: Int): String? {
        val cacheKey = "filteredRecipes_${diet}_${intolerance}_${minCalories}_${maxCalories}"
        return getOrFetchData(
            cacheKey,
            { filteredRecipesCache[cacheKey]?.takeIf { !isCacheExpired(it.timestamp) }?.response },
            {
                val url = "$baseUrl/complexSearch?diet=$diet&intolerances=$intolerance&minCalories=$minCalories&maxCalories=$maxCalories&apiKey=$apiKey"
                makeRequest(url)?.also {
                    filteredRecipesCache[cacheKey] = CachedResponse(it)
                }
            }
        )
    }

    private suspend fun getOrFetchData(
        cacheKey: String,
        getFromCache: () -> String?,
        fetchFromNetwork: suspend () -> String?
    ): String? {
        val lock = getLock(cacheKey)
        var result: String? = null
        var shouldFetch = false

        lock.withLock {
            getFromCache()?.let {
                Log.d("API", "Cache hit for $cacheKey")
                return it
            }

            val inProgress = inProgressRequests.computeIfAbsent(cacheKey) { MutableSharedFlow(replay = 1) }
            if (inProgress.subscriptionCount.value > 1) {
                Log.d("API", "Waiting for in-progress request for $cacheKey")
                result = null
            } else {
                shouldFetch = true
            }
        }

        if (shouldFetch) {
            result = fetchFromNetwork()
            val inProgress = inProgressRequests[cacheKey]
            if (inProgress != null) {
                CoroutineScope(Dispatchers.Default).launch {
                    Log.d("API", "Network fetch for $cacheKey")
                    inProgress.emit(result)
                    inProgressRequests.remove(cacheKey)
                }
            }
        } else {
            Log.d("API", "Returning in-progress result for $cacheKey")
            result = inProgressRequests[cacheKey]?.first()
        }

        return result
    }

    private suspend fun makeRequest(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()
                } else {
                    Log.e("API", "Failed to fetch data: ${response.code} - ${response.message}")
                    null // Log the error code and message
                }
            }
        } catch (e: IOException) {
            Log.e("API", "Error fetching data", e)
            null // Log exception details
        }
    }

    private fun isCacheExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > cacheExpirationTimeMillis
    }

    data class CachedResponse(val response: String, val timestamp: Long = System.currentTimeMillis())
    // Cache for recipes
//    private var recipesCache: String? = null
//    private val ingredientsCache = mutableMapOf<Int, List<Ingredient>>()
//
//    companion object {
//        fun createCustomClient(): OkHttpClient {
//            return OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)  // Increase connect timeout
//                .writeTimeout(30, TimeUnit.SECONDS)     // Increase write timeout
//                .readTimeout(30, TimeUnit.SECONDS)      // Increase read timeout
//                .build()
//        }
//    }
//
//    private val baseUrl = "https://api.spoonacular.com/recipes"
//    private val apiKey = "0a6354dbaba643d8a3e010e9c6bdf60a"  // Replace with your actual API keys
//
//
//    suspend fun getRecipes(ingredients: String): String? {
//        // Use cached data if available
//        if (recipesCache != null) return recipesCache
//
//        val encodedIngredients = URLEncoder.encode(ingredients, "UTF-8")
//        val url = "$baseUrl/findByIngredients?ingredients=$encodedIngredients&apiKey=$apiKey"
//        val request = Request.Builder().url(url).build()
//
//        client.newCall(request).execute().use { response ->
//            if (response.isSuccessful) {
//                recipesCache = response.body?.string()  // Cache the response
//                return recipesCache
//            } else {
//                throw Exception("Failed to fetch recipes: ${response.code} - ${response.message}")
//            }
//        }
//    }
//
//    suspend fun getRecipeDetails(recipeId: Int): String? {
//        val recipeRepository = RecipeRepository()
//        // Check if the recipe details are in the cache
//        val recipeDetails = parseRecipeFromCache(recipeId)
//        if (recipeDetails != null) return recipeDetails
//
//        // If not in cache, make an API call
//        val url = "$baseUrl/$recipeId/information?includeNutrition=false&apiKey=$apiKey"
//        val response = makeRequest(url)
//
//        if (response != null) {
//            // Parse the response and return instructions
//            return try {
//                recipeRepository.parseRecipeDetails(response).instructions
//            } catch (e: Exception) {
//                Log.e("API", "Error parsing recipe details: ${e.message}")
//                null
//            }
//        }
//        return null
//    }
//
//    private suspend fun makeRequest(url: String): String? {
//        val request = Request.Builder().url(url).build()
//        return client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) throw Exception("Unexpected code $response")
//            response.body?.string()
//        }
//    }
//
//    private var filteredRecipesCache: String? = null
//
//    suspend fun getFilteredRecipes(diet: String, intolerance: String, minCalories: Int, maxCalories: Int): String? {
//        // Check cache for filtered recipes
//        if (filteredRecipesCache != null) return filteredRecipesCache
//
//        val url = "$baseUrl/complexSearch?diet=$diet&intolerances=$intolerance&minCalories=$minCalories&maxCalories=$maxCalories&apiKey=$apiKey"
//        val response = makeRequest(url)
//
//        if (response != null) {
//            filteredRecipesCache = response
//        }
//        return response
//    }
//
//    private fun parseRecipeFromCache(recipeId: Int): String? {
//        recipesCache?.let {
//            val jsonArray = JSONArray(it)  // Assuming the root of your cache is a JSON array
//            for (i in 0 until jsonArray.length()) {
//                val recipe = jsonArray.getJSONObject(i)
//                if (recipe.getInt("id") == recipeId) {
//                    return recipe.toString()  // Return the recipe as a string if it matches the ID
//                }
//            }
//        }
//        return null  // Return null if no matching recipe is found
//    }
//
//
//    suspend fun getIngredients(recipeId: Int): List<Ingredient> {
//        // Check cache first for existing ingredients
//        ingredientsCache[recipeId]?.let { cachedIngredients ->
//            return cachedIngredients
//        }
//
//        // If not cached, fetch from the API
//        val url = "$baseUrl/$recipeId/ingredientWidget.json?apiKey=$apiKey"
//        val request = Request.Builder().url(url).build()
//
//        try {
//            val response = client.newCall(request).execute()
//            if (response.isSuccessful) {
//                response.body?.string()?.let { responseBody ->
//                    val ingredients = RecipeRepository().parseIngredients(responseBody)
//                    // Cache the ingredients after successful fetch
//                    ingredientsCache[recipeId] = ingredients
//                    return ingredients
//                } ?: return emptyList()  // Handle null responseBody
//            } else {
//                Log.e("API", "Failed to fetch ingredients: HTTP ${response.code}")
//            }
//        } catch (e: Exception) {
//            Log.e("API", "Exception in getIngredients: ${e.message}")
//        }
//        return emptyList()  // Return empty list if fetching fails
//    }

    fun classifyImages(context: Context, uris: List<Uri>, callback: (List<List<String>>) -> Unit) {
        val imagesBase64 = uris.mapNotNull { uri ->
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }

        if (imagesBase64.isEmpty()) {
            throw IllegalArgumentException("At least one image is required.")
        }

        val json = JSONObject()
        json.put("images", JSONArray(imagesBase64))
        json.put("num_results", 1)  // Ensure only one result per image

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toString().toRequestBody(mediaType)

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("http://192.168.0.113:5000/classify-image")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("classifyImages", "Failed to classify images", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val results = parseResults(responseBody)

                    Handler(Looper.getMainLooper()).post {
                        callback(results)
                    }
                } else {
                    Log.e("classifyImages", "Failed to classify images. Response code: ${response.code}")
                }
            }
        })
    }


    fun parseResults(responseBody: String?): List<List<String>> {
        val results = mutableListOf<List<String>>()
        responseBody?.let {
            try {
                val jsonObject = JSONObject(it)
                val jsonArray = jsonObject.getJSONArray("ingredients")
                for (i in 0 until jsonArray.length()) {
                    val labels = mutableListOf<String>()
                    val imageObject = jsonArray.getJSONObject(i)
                    val ingredientsArray = imageObject.getJSONArray("ingredients")
                    for (j in 0 until ingredientsArray.length()) {
                        labels.add(ingredientsArray.getString(j))
                    }
                    results.add(labels)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return results
    }
}