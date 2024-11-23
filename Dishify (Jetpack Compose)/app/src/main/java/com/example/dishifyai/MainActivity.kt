package com.example.dishifyai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.dishifyai.ui.theme.DishifyAITheme
import com.example.dishifyai.ui.screens.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.dishifyai.ui.screens.recipe.Greeting
import com.example.dishifyai.ui.screens.recipe.RecipeDetailView
import com.example.dishifyai.ui.screens.recipe.RecipeResultsPage
import com.example.dishifyai.ui.screens.recipe.ScannedItemsPage
import com.example.dishifyai.viewmodel.RecipeViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DishifyAITheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        MainAppNavHost()
    }
}

@Composable
fun MainAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    recipeViewModel: RecipeViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = determineStartDestination(),
        modifier = modifier
    ) {
        unauthenticatedGraph(navController)
        authenticatedGraph(navController, recipeViewModel)
        recipeNavigation(navController, recipeViewModel)
        composable(route = "favorites") {
            FavoritesPage().FavoritesScreen(navController)
        }
    }
}

fun NavGraphBuilder.recipeNavigation(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    composable(route = NavigationRoutes.Recipe.Greeting.route) {
        Greeting(navController, recipeViewModel)
    }
    composable(
        route = NavigationRoutes.Recipe.ScannedItemsPage.route + "/{scannedItems}",
        arguments = listOf(navArgument("scannedItems") { type = NavType.StringType })
    ) { backStackEntry ->
        val scannedItems =
            backStackEntry.arguments?.getString("scannedItems")?.split(",") ?: listOf()
        ScannedItemsPage(navController, scannedItems, recipeViewModel)
    }
    composable(route = NavigationRoutes.Recipe.RecipeResultsPage.route) {
        RecipeResultsPage(recipeViewModel, navController)
    }
    composable(
        route = "recipeDetail/{recipeId}",
        arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
    ) { backStackEntry ->
        val recipeId = backStackEntry.arguments?.getInt("recipeId")
            ?: throw IllegalStateException("Recipe ID not found")
        RecipeDetailView(
            recipeId = recipeId,
            viewModel = viewModel(),
            navController = navController
        )
    }
}

private fun determineStartDestination(): String {
    return if (userIsLoggedIn()) {
        NavigationRoutes.Authenticated.NavigationRoute.route
    } else {
        NavigationRoutes.Unauthenticated.NavigationRoute.route
//        NavigationRoutes.Recipe.RecipeResultsPage.route
    }
}

private fun userIsLoggedIn(): Boolean {
    return false
}