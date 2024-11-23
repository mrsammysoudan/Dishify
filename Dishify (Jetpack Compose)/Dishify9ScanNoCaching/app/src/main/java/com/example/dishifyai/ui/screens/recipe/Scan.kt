package com.example.dishifyai.ui.screens.recipe

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.material3.Text
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dishifyai.R
import com.example.dishifyai.API
import com.example.dishifyai.ui.screens.BottomNavigationBar
import com.example.dishifyai.viewmodel.RecipeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.animateLottieCompositionAsState

@Composable
fun FullScreenLottieAnimation(isVisible: Boolean) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)) // Semi-transparent background
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
            val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

            LottieAnimation(
                composition,
                progress,
                modifier = Modifier.size(300.dp) // Adjust size as needed
            )
        }
    }
}

@Composable
fun Greeting(navController: NavHostController, recipeViewModel: RecipeViewModel) {
    val context = LocalContext.current
    val imageUris = remember { mutableStateListOf<Uri?>(null, null, null, null, null, null, null, null, null) }
    val showDialog = remember { mutableStateOf(false) }
    val selectedBoxIndex = remember { mutableStateOf(-1) }
    val isLoading = remember { mutableStateOf(false) }
    val showScannedItems = remember { mutableStateOf(false) }
    val scannedItems = remember { mutableStateListOf<String>() }
    var sliderValue by remember { mutableStateOf(1f) }
    val api = API()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Image with content scale adjustment
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp) // Try adjusting the size
                    .padding(0.dp), // Ensure no extra padding
                contentScale = ContentScale.Fit // Adjust how the image fits within the given size
            )

            // "DISHIFY" Text
            Text(
                text = "DISHIFY",
                modifier = Modifier.padding(top = 4.dp), // Reduced top padding
                style = TextStyle(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp)) // Space between text and grid

            Text(
                text = "Start by Adding Pictures:",
                style = TextStyle(
                    fontSize = 20.sp, // Increased font size
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Grid of clickable boxes
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Takes up all available space
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(imageUris) { index, uri ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .border(2.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable {
                                selectedBoxIndex.value = index
                                showDialog.value = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        uri?.let {
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = "Selected Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(
                                        scaleX = 1f, // Adjust the scale as needed
                                        scaleY = 1f // Adjust the scale as needed
                                    )
                            )
                        } ?: Icon(Icons.Default.Add, contentDescription = "Add Image")
                    }
                }
            }

            // "SCAN INGREDIENTS" Button
            Button(
                onClick = {
                    isLoading.value = true

                    CoroutineScope(Dispatchers.IO).launch {
                        val nonNullUris = imageUris.filterNotNull()
                        val results = mutableListOf<List<String>>()

                        api.classifyImages(context, nonNullUris) { labels ->
                            results.addAll(labels)

                            CoroutineScope(Dispatchers.Main).launch {
                                isLoading.value = false
                                scannedItems.clear()
                                scannedItems.addAll(results.flatten())
                                showScannedItems.value = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp)
                    .height(60.dp),
                enabled = !isLoading.value && imageUris.any { it != null }
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("SCAN INGREDIENTS", color = Color.White)
                }
            }
        }

        FullScreenLottieAnimation(isVisible = isLoading.value)

        // This launcher is used to get content from the gallery
        val openGalleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                if (selectedBoxIndex.value >= 0) {
                    imageUris[selectedBoxIndex.value] = it
                    showDialog.value = false // Close the dialog automatically
                }
            }
        }

        // This launcher is used to request permission to access the external storage
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openGalleryLauncher.launch("image/*")
            } else {
                // Handle permission denial here
            }
        }

        // This launcher is for taking a photo with the camera
        val takePictureLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap: Bitmap? ->
            bitmap?.let {
                // Here you would save the bitmap to a file and get the Uri of that file
                val imageUri = saveBitmapToFileAndGetUri(context, it)
                if (selectedBoxIndex.value >= 0) {
                    imageUris[selectedBoxIndex.value] = imageUri
                    showDialog.value = false // Close the dialog automatically
                }
            }
        }

        // Dialog to choose between Gallery or Camera
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Choose an option") },
                text = {
                    Column {
                        TextButton(onClick = {
                            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }) {
                            Text("Open Gallery")
                        }
                        TextButton(onClick = {
                            takePictureLauncher.launch(null)
                        }) {
                            Text("Open Camera")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Scanned Items Modal Dialog
        if (showScannedItems.value) {
            AlertDialog(
                onDismissRequest = { showScannedItems.value = false },
                title = { Text("Scanned Items") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth() // Fill the width
                            .heightIn(min = 100.dp, max = 300.dp) // Control the height
                    ) {
                        ScannedItemsPage(navController, scannedItems, recipeViewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showScannedItems.value = false },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        ) {
                            Text("Close")
                        }
                    }
                },
                confirmButton = {}, // Empty Composable for confirmButton
                dismissButton = {}, // Empty Composable for dismissButton
                modifier = Modifier
                    .fillMaxWidth() // Fill the width of the screen
                    .wrapContentHeight(Alignment.Bottom) // Align to bottom with wrapped content height
                    .padding(bottom = 50.dp) // Padding from bottom of screen
            )
        }

        // Bottom Navigation Bar
        BottomNavigationBar(navController, Modifier.align(Alignment.BottomCenter))
    }
}

fun saveBitmapToFileAndGetUri(context: Context, bitmap: Bitmap): Uri {
    // The public directory where the image will be saved
    val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    // Create a unique file name
    val imageFile = File(imagesDir, "image_${System.currentTimeMillis()}.jpg")

    // Write the bitmap to the file
    FileOutputStream(imageFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }

    // Return the Uri of the saved file
    return FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        imageFile
    )
}

@Composable
fun ScannedItemsPage(
    navController: NavHostController,
    scannedItems: List<String>,
    recipeViewModel: RecipeViewModel
) {
    val recipes by recipeViewModel.recipes.collectAsState()
    val isLoading by recipeViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scanned Items:")
        Spacer(modifier = Modifier.height(16.dp))

        // Display the scanned items
        scannedItems.forEach { item ->
            Text(item)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to find recipes
        Button(
            onClick = {
                val ingredientQuery = scannedItems.joinToString(",")
                Log.d("ScannedItemsPage", "Ingredient Query: $ingredientQuery")
                recipeViewModel.fetchRecipeResults(ingredientQuery)
                navController.navigate("recipeResultsPage")
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Find Recipes", color = Color.White)
            }
        }
    }

    FullScreenLottieAnimation(isVisible = isLoading)
}


