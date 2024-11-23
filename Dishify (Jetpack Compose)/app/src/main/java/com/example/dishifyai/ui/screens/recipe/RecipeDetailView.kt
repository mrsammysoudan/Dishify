package com.example.dishifyai.ui.screens.recipe

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.dishifyai.data.Ingredient
import com.example.dishifyai.data.Recipe
import com.example.dishifyai.data.RecipeDetails
import com.example.dishifyai.ui.theme.Pink
import com.example.dishifyai.ui.theme.md_theme_light_primary
import com.example.dishifyai.ui.theme.md_theme_light_primaryContainer
import com.example.dishifyai.ui.theme.md_theme_light_secondary
import com.example.dishifyai.viewmodel.RecipeViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import kotlin.math.max
import kotlin.math.min
//import com.example.dishifyai.R

@Composable
fun RecipeDetailView(recipeId: Int, viewModel: RecipeViewModel, navController: NavController) {
    val recipeDetailsState by viewModel.fetchRecipeDetails(recipeId).observeAsState()
    val recipeDetails = recipeDetailsState

    val recipeState by viewModel.getBasicRecipeInfo(recipeId).observeAsState()
    val recipe = recipeState

    val ingredientsState by viewModel.getIngredients(recipeId).observeAsState(initial = emptyList())
    val ingredients = ingredientsState

    Log.d("RecipeDetailView", "Recipe details state: $recipeDetails")
    Log.d("RecipeDetailView", "Basic recipe info state: $recipe")

    val scrollState = rememberLazyListState()

    ProvideWindowInsets {
        Surface(color = Color.White) {
            Box {
                Content(recipeDetails, recipe, ingredients, scrollState)
                recipe?.let {
                    ParallaxToolbar(recipe, scrollState)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParallaxToolbar(recipe: Recipe, scrollState: LazyListState) {
    val imageHeight = AppBarExpendedHeight - AppBarCollapsedHeight
    val maxOffset = with(LocalDensity.current) { imageHeight.roundToPx() } - LocalWindowInsets.current.systemBars.layoutInsets.top
    val offset = min(scrollState.firstVisibleItemScrollOffset, maxOffset)
    val offsetProgress = max(0f, offset * 3f - 2f * maxOffset) / maxOffset

    Column {
        Box(
            Modifier
                .height(imageHeight)
                .graphicsLayer { alpha = 1f - offsetProgress }
        ) {
            Image(
                painter = rememberAsyncImagePainter(recipe.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                Pair(0.4f, Color.Transparent),
                                Pair(1f, Color.White)
                            )
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${recipe.servings} servings",
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(md_theme_light_primary)
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                )
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .height(AppBarCollapsedHeight),
            verticalArrangement = Arrangement.Center
        ) {
            if (offset == maxOffset) {
                Text(
                    text = recipe.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = (16 + 28 * offsetProgress).dp)
                        .scale(1f - 0.25f * offsetProgress)
                )
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(AppBarCollapsedHeight)
            .padding(horizontal = 16.dp)
    ) {
        CircularButton(Icons.Default.ArrowBack)
        CircularButton(Icons.Default.Favorite, color = Color.Red)
    }
}

@Composable
fun Content(
    recipeDetails: RecipeDetails?,
    recipe: Recipe?,
    ingredients: List<Ingredient>,
    scrollState: LazyListState
) {
    LazyColumn(contentPadding = PaddingValues(top = AppBarExpendedHeight), state = scrollState) {
        item {
            if (recipeDetails != null && recipe != null) {
                BasicInfo(recipe)
                Spacer(modifier = Modifier.height(AppBarCollapsedHeight))
                Description(recipeDetails)
                IngredientsHeader()
                IngredientsList(ingredients)
            } else {
                CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun CircularButton(
    icon: ImageVector,
    color: Color = Color.Black,
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = color),
        elevation = elevation,
        modifier = Modifier
            .width(38.dp)
            .height(38.dp)
    ) {
        Icon(icon, contentDescription = null)
    }
}


@Composable
fun BasicInfo(recipe: Recipe) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        InfoColumn(Icons.Default.AccessTime, "${recipe.readyInMinutes} mins")
        InfoColumn(Icons.Default.People, "${recipe.servings} servings")
        InfoColumn(Icons.Default.Favorite, "Health: ${recipe.healthScore}")
    }
}

@Composable
fun InfoColumn(icon: ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Pink,
            modifier = Modifier.height(24.dp)
        )
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Description(recipeDetails: RecipeDetails) {
    val cleanedInstructions = cleanInstructions(recipeDetails.instructions)
    Log.d("Description", "Cleaned Instructions: $cleanedInstructions") // Add this line to log the cleaned instructions
    Text(
        text = cleanedInstructions,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

fun cleanInstructions(instructions: String): String {
    // Remove HTML tags
    var cleaned = instructions.replace(Regex("<[^>]*>"), "")
    // Remove promotional content
    val endIndex = cleaned.indexOf("Please subscribe")
    if (endIndex != -1) {
        cleaned = cleaned.substring(0, endIndex)
    }
    return cleaned.trim()
}

@Composable
fun IngredientsHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray)
            .fillMaxWidth()
            .height(44.dp)
    ) {
        TabButton("Ingredients", true, Modifier.weight(1f))
        TabButton("Tools", false, Modifier.weight(1f))
        TabButton("Steps", false, Modifier.weight(1f))
    }
}

@Composable
fun IngredientsList(ingredients: List<Ingredient>) {
    EasyGrid(nColumns = 3, items = ingredients) {
        IngredientCard(it.image, it.name, "", Modifier)
    }
}

@Composable
fun <T> EasyGrid(nColumns: Int, items: List<T>, content: @Composable (T) -> Unit) {
    Column(Modifier.padding(16.dp)) {
        for (i in items.indices step nColumns) {
            Row {
                for (j in 0 until nColumns) {
                    if (i + j < items.size) {
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.weight(1f)
                        ) {
                            content(items[i + j])
                        }
                    } else {
                        Spacer(Modifier.weight(1f, fill = true))
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientCard(
    iconUrl: String,
    title: String,
    subtitle: String,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .padding(bottom = 8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(iconUrl),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(text = title, modifier = Modifier.width(100.dp), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text(text = subtitle, color = DarkGray, modifier = Modifier.width(100.dp), fontSize = 14.sp)
    }
}

@Composable
fun TabButton(text: String, active: Boolean, modifier: Modifier) {
    Button(
        onClick = { /*TODO*/ },
        shape = RoundedCornerShape(4.dp),
        modifier = modifier.fillMaxHeight(),
        elevation = null,
        colors = if (active) ButtonDefaults.buttonColors(
            containerColor = Pink,
            contentColor = Color.White
        ) else ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = Color.DarkGray
        )
    ) {
        Text(text)
    }
}

// Replace with actual values or resources
val AppBarCollapsedHeight = 56.dp
val AppBarExpendedHeight = 400.dp