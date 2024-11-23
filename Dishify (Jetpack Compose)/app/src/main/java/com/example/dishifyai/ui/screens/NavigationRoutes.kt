package com.example.dishifyai.ui.screens

sealed class NavigationRoutes {
    // Unauthenticated Routes
    sealed class Unauthenticated(val route: String) : NavigationRoutes() {
        object NavigationRoute : Unauthenticated(route = "unauthenticated")
        object Login : Unauthenticated(route = "login")
        object Registration : Unauthenticated(route = "registration")
    }

    // Authenticated Routes
    sealed class Authenticated(val route: String) : NavigationRoutes() {
        object NavigationRoute : Authenticated(route = "authenticated")
        object Dashboard : Authenticated(route = "Dashboard")
        object Payment : Authenticated(route = "payment")
        object Profile : Authenticated(route = "profile")
    }

    // Recipe related routes
    sealed class Recipe(val route: String) : NavigationRoutes() {
        object Greeting : Recipe("greeting")
        object ScannedItemsPage : Recipe("scannedItemsPage/{scannedItems}")
        object RecipeResultsPage : Recipe("recipeResultsPage")
        object RecipeDetail : Recipe("recipeDetail/{recipeId}")
    }
}
