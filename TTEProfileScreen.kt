@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.railid.tte

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TTEProfileScreen(
    navController: NavController,
    onNavigateHome: () -> Unit
) {
    val context = LocalContext.current
    val backgroundWhite = Color.White
    val darkBlue = Color(0xFF001F3F)

    var officerName by remember { mutableStateOf("TTE Officer") }
    var employeeId by remember { mutableStateOf("TTE-12345") }
    var route by remember { mutableStateOf("Chennai - Bangalore") }

    // Optionally, load actual TTE profile from SharedPreferences if implemented
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("RailIdPrefs", Context.MODE_PRIVATE)
        officerName = prefs.getString("tteName", "TTE Officer") ?: "TTE Officer"
        employeeId = prefs.getString("tteId", "TTE-12345") ?: "TTE-12345"
        route = prefs.getString("tteRoute", "Chennai - Bangalore") ?: "Chennai - Bangalore"
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = darkBlue) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
                    label = { Text("Home", color = Color.White) },
                    selected = false,
                    onClick = onNavigateHome
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White) },
                    label = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = true,
                    onClick = {}
                )
            }
        },
        containerColor = backgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundWhite)
                .padding(24.dp)
        ) {
            Text("Profile", fontSize = 28.sp, color = darkBlue, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            ProfileItem(label = "Name", value = officerName, color = darkBlue)
            ProfileItem(label = "Employee ID", value = employeeId, color = darkBlue)
            ProfileItem(label = "Route", value = route, color = darkBlue)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Clear TTE login data and navigate back to login screen
                    val prefs = context.getSharedPreferences("RailIdPrefs", Context.MODE_PRIVATE)
                    prefs.edit().remove("tteId").apply()
                    navController.navigate("login") {
                        popUpTo("tteHome") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(label, fontSize = 18.sp, color = color)
        Text(value, fontSize = 22.sp, color = color, fontWeight = FontWeight.Medium)
    }
}
