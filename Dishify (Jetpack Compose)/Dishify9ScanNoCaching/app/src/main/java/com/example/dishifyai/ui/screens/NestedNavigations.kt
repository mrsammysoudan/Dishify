package com.example.dishifyai.ui.screens

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.dishifyai.ui.screens.dashboard.DashboardScreen
import com.example.dishifyai.ui.screens.unauthenticated.login.LoginScreen
import com.example.dishifyai.ui.screens.unauthenticated.registration.RegistrationScreen
import com.example.dishifyai.ui.screens.recipe.Greeting
import com.example.dishifyai.viewmodel.RecipeViewModel

/**
 * Login, registration, forgot password screens nav graph builder
 * (Unauthenticated user)
 */
fun NavGraphBuilder.unauthenticatedGraph(navController: NavController) {

    navigation(
        route = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Unauthenticated.Login.route
    ) {

        // Login
        composable(route = NavigationRoutes.Unauthenticated.Login.route) {
            LoginScreen(
                onNavigateToRegistration = {
                    navController.navigate(route = NavigationRoutes.Unauthenticated.Registration.route)
                },
                onNavigateToForgotPassword = {},
                onNavigateToAuthenticatedRoute = {
                    navController.navigate(route = NavigationRoutes.Authenticated.NavigationRoute.route) {
                        popUpTo(route = NavigationRoutes.Unauthenticated.NavigationRoute.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        // Registration
        composable(route = NavigationRoutes.Unauthenticated.Registration.route) {
            RegistrationScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToAuthenticatedRoute = {
                    navController.navigate(route = NavigationRoutes.Authenticated.NavigationRoute.route) {
                        popUpTo(route = NavigationRoutes.Unauthenticated.NavigationRoute.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

/**
 * Authenticated screens nav graph builder
 */
// Adjusted start destination for authenticated users to Greeting screen
fun NavGraphBuilder.authenticatedGraph(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    navigation(
        startDestination = NavigationRoutes.Recipe.Greeting.route,
        route = NavigationRoutes.Authenticated.NavigationRoute.route
    ) {
        composable(route = NavigationRoutes.Recipe.Greeting.route) {
            Greeting(navController, recipeViewModel)
        }
        composable(route = NavigationRoutes.Authenticated.Payment.route) {
            PaymentPage().PaymentScreen(navController)
        }
        composable(route = NavigationRoutes.Authenticated.Profile.route) {
            ProfilePage().ProfileScreen(navController)
        }
    }
}