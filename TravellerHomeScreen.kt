package com.example.railid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
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
fun TravellerHomeScreen(
    userName: String,
    onNavigateProfile: () -> Unit,
    onNavigateToScreen: (String) -> Unit
) {
    val backgroundBlue = Color(0xFF001F3F)
    val white = Color.White

    // Now using route keys (not display titles)
    val actionButtons = listOf(
        Triple("booking", Icons.Default.AddCircle, Color(0xFF1ABC9C)),
        Triple("cancelTicket", Icons.Default.Close, Color(0xFFE74C3C)),
        Triple("myBookings", Icons.AutoMirrored.Filled.List, Color(0xFFF39C12)),
        Triple("bookingHistory", Icons.Default.History, Color(0xFF9B59B6))
    )

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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundBlue)
        ) {
            Text(
                text = "Hi, $userName 👋",
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
                    .padding(16.dp)
            ) {
                Text(
                    text = "Quick Actions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = backgroundBlue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                for ((route, icon, color) in actionButtons) {
                    val displayTitle = when (route) {
                        "booking" -> "Book Tickets"
                        "cancelTicket" -> "Cancel Ticket"
                        "myBookings" -> "My Bookings"
                        "bookingHistory" -> "History"
                        else -> route
                    }

                    ActionCard(
                        title = displayTitle,
                        icon = icon,
                        backgroundColor = color,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        onNavigateToScreen(route) // correct route key
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
