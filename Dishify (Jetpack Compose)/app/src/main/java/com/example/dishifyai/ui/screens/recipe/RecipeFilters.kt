package com.example.dishifyai.ui.screens.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dishifyai.viewmodel.RecipeViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.dishifyai.R
import com.example.dishifyai.ui.theme.DishifyAITheme
import com.google.accompanist.flowlayout.FlowRow


@Composable
fun FilterScreen(
    viewModel: RecipeViewModel,
    onDismiss: () -> Unit
) {
    val selectedDietaryOption by viewModel.selectedDietaryOption.collectAsState()
    val selectedIntoleranceOption by viewModel.selectedIntoleranceOption.collectAsState()

    var localSelectedDietaryOption by remember { mutableStateOf(selectedDietaryOption) }
    var localSelectedIntoleranceOption by remember { mutableStateOf(selectedIntoleranceOption) }
    var cuisine by remember { mutableStateOf("") }
    var excludeCuisine by remember { mutableStateOf("") }
    var includeIngredients by remember { mutableStateOf("") }
    var excludeIngredients by remember { mutableStateOf("") }
    var maxCalories by remember { mutableStateOf(0f) }
    var maxReadyTime by remember { mutableStateOf(60f) }

    Dialog(onDismissRequest = onDismiss) {
        DishifyAITheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close"
                                )
                            }
                        },
                        title = {
                            Text(
                                text = "Filters",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.h6
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    // Reset filters
                                    localSelectedDietaryOption = ""
                                    localSelectedIntoleranceOption = ""
                                    cuisine = ""
                                    excludeCuisine = ""
                                    includeIngredients = ""
                                    excludeIngredients = ""
                                    maxCalories = 0f
                                    maxReadyTime = 60f
                                }
                            ) {
                                Text(
                                    text = "Reset",
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        },
                        backgroundColor = MaterialTheme.colors.surface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Main Filters Section
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 8.dp)
                    ) {
                        FilterChip(
                            label = "Vegan",
                            icon = R.drawable.vegan,
                            isSelected = localSelectedDietaryOption == "Vegan",
                            onSelected = {
                                localSelectedDietaryOption = "Vegan"
                                viewModel.setSelectedDietaryOption("Vegan")
                            }
                        )
                        FilterChip(
                            label = "Vegetarian",
                            icon = R.drawable.vegetarian,
                            isSelected = localSelectedDietaryOption == "Vegetarian",
                            onSelected = {
                                localSelectedDietaryOption = "Vegetarian"
                                viewModel.setSelectedDietaryOption("Vegetarian")
                            }
                        )
                        FilterChip(
                            label = "Calories",
                            icon = R.drawable.calories,
                            isSelected = false,
                            onSelected = { /* Handle Calories filter */ }
                        )
                        // Add more main filters as needed
                    }

                    // Dietary Filter Section
                    FilterSection(
                        title = "Dietary",
                        options = listOf("Vegetarian", "Vegan", "Gluten Free"),
                        selectedOption = localSelectedDietaryOption,
                        onOptionSelected = { option ->
                            localSelectedDietaryOption = option
                            viewModel.setSelectedDietaryOption(option)
                        }
                    )

                    // Intolerance Filter Section
                    FilterSection(
                        title = "Intolerance",
                        options = listOf("Dairy", "Egg", "Gluten"),
                        selectedOption = localSelectedIntoleranceOption,
                        onOptionSelected = { option ->
                            localSelectedIntoleranceOption = option
                            viewModel.setSelectedIntoleranceOption(option)
                        }
                    )

                    // Cuisine Filter Section
                    TextField(
                        value = cuisine,
                        onValueChange = { cuisine = it },
                        label = { Text("Cuisine") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Exclude Cuisine Filter Section
                    TextField(
                        value = excludeCuisine,
                        onValueChange = { excludeCuisine = it },
                        label = { Text("Exclude Cuisine") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Include Ingredients Filter Section
                    TextField(
                        value = includeIngredients,
                        onValueChange = { includeIngredients = it },
                        label = { Text("Include Ingredients") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Exclude Ingredients Filter Section
                    TextField(
                        value = excludeIngredients,
                        onValueChange = { excludeIngredients = it },
                        label = { Text("Exclude Ingredients") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Max Calories Slider
                    MaxCalories(
                        sliderPosition = maxCalories,
                        onValueChanged = { newValue -> maxCalories = newValue }
                    )

                    // Max Ready Time Slider
                    MaxReadyTime(
                        sliderPosition = maxReadyTime,
                        onValueChanged = { newValue -> maxReadyTime = newValue }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Apply filters and dismiss
                            viewModel.fetchFilteredRecipes(
                                localSelectedDietaryOption,
                                localSelectedIntoleranceOption,
                                0,
                                maxCalories.toInt()
                            )
                            onDismiss()
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
fun MaxCalories(sliderPosition: Float, onValueChanged: (Float) -> Unit) {
    Text(
        text = "Max Calories",
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Slider(
        value = sliderPosition,
        onValueChange = onValueChanged,
        valueRange = 0f..2000f,
        steps = 20,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.primary,
            activeTrackColor = MaterialTheme.colors.primary
        )
    )
}

@Composable
fun MaxReadyTime(sliderPosition: Float, onValueChanged: (Float) -> Unit) {
    Text(
        text = "Max Ready Time (minutes)",
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Slider(
        value = sliderPosition,
        onValueChange = onValueChanged,
        valueRange = 0f..240f,
        steps = 20,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.primary,
            activeTrackColor = MaterialTheme.colors.primary
        )
    )
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp
    ) {
        options.forEach { option ->
            FilterChip(
                label = option,
//                icon = Icons.Default.FilterList,  // Add the filter icon
                isSelected = option == selectedOption,
                onSelected = { onOptionSelected(option) }
            )
        }
    }
}



@Composable
fun FilterChip(
    label: String,
    icon: Int? = null,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    val contentColor = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
    Surface(
        modifier = Modifier
            .selectable(selected = isSelected, onClick = onSelected)
            .padding(4.dp),
        color = backgroundColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (icon != null) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.body2
            )
        }
    }
}
@Preview("Filter Screen")
@Composable
fun FilterScreenPreview() {
    DishifyAITheme {
        FilterScreen(viewModel = RecipeViewModel(), onDismiss = {})
    }
}