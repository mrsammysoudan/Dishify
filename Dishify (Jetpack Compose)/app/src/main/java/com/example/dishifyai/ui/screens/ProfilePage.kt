package com.example.dishifyai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.dishifyai.R

class ProfilePage {
    @Composable
    fun ProfileScreen(navController: NavHostController) {
        val userName = "Sammy Soudan" // You can replace this with a dynamic user name
        val totalScansLeft = 0 // Replace with actual value
        val currentSubscription = "Free Plan" // Replace with actual subscription info

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val maxHeight = maxHeight

            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // User Info Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = userName,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Icon",
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.Gray, shape = CircleShape)
                                .padding(8.dp)
                        )
                    }

                    // Total Scans Left Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp) // Adjust horizontal padding as needed
                            .background(Color(0xFFDADADA), shape = RoundedCornerShape(12.dp))
                            .padding(16.dp)
                            .padding(vertical = 16.dp) // Added vertical padding for more height
                            .height(IntrinsicSize.Min)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Total Scans Left",
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Total Scans Left: $totalScansLeft",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Options Row
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        ProfileOption("Favorites", Icons.Default.Favorite) {
                            navController.navigate("favorites")
                        }
                        ProfileOption("Wallet", Icons.Default.AccountBalanceWallet)
                        ProfileOption("My Recipes", Icons.Default.Book) {
                            navController.navigate("myRecipes")
                        }
                    }

                    Divider(color = Color.LightGray, thickness = 3.dp, modifier = Modifier.padding(vertical = 3.dp))

                    // Profile List Items
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        ProfileListItem(Icons.Default.Subscriptions, "Current Subscription: $currentSubscription")
                        ProfileListItem(Icons.Default.LocalOffer, "Promotions")
                        ProfileListItem(Icons.Default.CardGiftcard, "Send a Gift")
                        ProfileListItem(Icons.Default.Help, "Help")
                        ProfileListItem(Icons.Default.PersonAdd, "Invite Friends", "Get 50 extra Scans")
                        ProfileListItem(Icons.Default.Edit, "Edit Account")
                        ProfileListItem(Icons.Default.Logout, "Log out")
                    }
                }

                // Bottom Navigation Bar
                BottomNavigationBar(
                    navController,
                    Modifier
                        .fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun ProfileOption(title: String, icon: ImageVector, onClick: () -> Unit = {}) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            )
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    @Composable
    fun ProfileListItem(icon: ImageVector, title: String, subtitle: String? = null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier
                    .size(32.dp)  // Increased the size of the icon
                    .padding(end = 16.dp)
            )
            Column {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                subtitle?.let {
                    Text(text = it, fontSize = 14.sp, color = Color.Gray)
                }
            }
        }
    }
}
