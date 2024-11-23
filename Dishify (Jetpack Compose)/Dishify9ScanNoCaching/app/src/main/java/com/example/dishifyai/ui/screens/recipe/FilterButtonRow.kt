package com.example.dishifyai.ui.screens.recipe

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dishifyai.R

@Composable
fun FilterButtonRow(
    selectedDietaryOption: String,
    onDietaryOptionSelected: (String) -> Unit,
    onFilterButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onFilterButtonClick,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text("All Filters")
        }
        FilterChip(
            label = "Vegan",
            icon = R.drawable.vegan,
            isSelected = selectedDietaryOption == "Vegan",
            onSelected = { onDietaryOptionSelected("Vegan") }
        )
        FilterChip(
            label = "Vegetarian",
            icon = R.drawable.vegetarian,
            isSelected = selectedDietaryOption == "Vegetarian",
            onSelected = { onDietaryOptionSelected("Vegetarian") }
        )
        FilterChip(
            label = "Gluten Free",
            icon = R.drawable.gluten_free,
            isSelected = selectedDietaryOption == "Gluten Free",
            onSelected = { onDietaryOptionSelected("Gluten Free") }
        )
        FilterChip(
            label = "Calories",
            icon = R.drawable.calories,
            isSelected = false,
            onSelected = { /* Handle Calories filter */ }
        )
        // Add more main filters as needed
    }
}