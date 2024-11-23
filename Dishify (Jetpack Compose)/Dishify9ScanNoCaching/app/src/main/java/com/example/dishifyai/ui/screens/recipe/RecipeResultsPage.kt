package com.example.dishifyai.ui.screens.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.dishifyai.viewmodel.RecipeViewModel
import com.example.dishifyai.data.Recipe
import com.example.dishifyai.ui.screens.BottomNavigationBar

@Composable
fun RecipeResultsPage(viewModel: RecipeViewModel, navHostController: NavHostController) {
    val recipes by viewModel.recipes.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    val selectedDietaryOption by viewModel.selectedDietaryOption.collectAsState()

    Box(modifier = Modifier.fillMaxHeight()) {
        Column(modifier = Modifier.fillMaxHeight()) {
            FilterButtonRow(
                selectedDietaryOption = selectedDietaryOption,
                onDietaryOptionSelected = viewModel::setSelectedDietaryOption,
                onFilterButtonClick = { showFilterDialog = true }
            )
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(recipes) { recipe ->
                    RecipeCard(recipe = recipe) {
                        navHostController.navigate("recipeDetail/${recipe.id}")
                    }
                }
            }
        }

        // Show filter dialog
        if (showFilterDialog) {
            FilterScreen(
                viewModel = viewModel,
                onDismiss = { showFilterDialog = false }
            )
        }

        BottomNavigationBar(
            navController = navHostController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column {
            val painter = rememberAsyncImagePainter(model = recipe.imageUrl)
            Image(
                painter = painter,
                contentDescription = "${recipe.title} image",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp),
                contentScale = ContentScale.Crop
            )
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is AsyncImagePainter.State.Error -> {
                    Text(
                        "Image failed to load",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> Unit
            }
            Text(
                text = recipe.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

