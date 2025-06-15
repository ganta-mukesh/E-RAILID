@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.railid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TTEHomeScreen(
    userName: String,
    onNavigateProfile: () -> Unit,
    onNavigateToScreen: (String) -> Unit
) {
    val backgroundBlue = Color(0xFF001F3F)
    val white = Color.White

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = backgroundBlue) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = white) },
                    label = { Text("Home", color = white, fontWeight = FontWeight.Bold) },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = white) },
                    label = { Text("Profile", color = white) },
                    selected = false,
                    onClick = onNavigateProfile
                )
            }
        },
        containerColor = backgroundBlue
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundBlue)
        ) {
            Text(
                text = "Hi, TTE Officer 👋",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = white,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(white)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "TTE Actions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = backgroundBlue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val actions = listOf(
                    Triple("All Tickets", Icons.AutoMirrored.Filled.List, "allTickets"),
                    Triple("Verify Tickets", Icons.Default.Verified, "verifyTicket")
                )

                actions.forEach { (title, icon, route) ->
                    ActionCard(title = title, icon = icon) {
                        onNavigateToScreen(route)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    val cardColors = mapOf(
        "All Tickets" to Color(0xFF1E88E5),
        "Verify Tickets" to Color(0xFF43A047)
    )
    val color = cardColors[title] ?: Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp)
        ) {
            Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(42.dp))
            Spacer(modifier = Modifier.width(24.dp))
            Text(title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}
