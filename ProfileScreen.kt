package com.example.railid

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.json.JSONObject
import com.example.railid.utils.sha256


@Composable
fun ProfileScreen(
    onNavigateHome: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("RailIdPrefs", Context.MODE_PRIVATE)

    val primaryBlue = Color(0xFF001F3F)
    val white = Color.White

    val usersJson = prefs.getString("users", "{}") ?: "{}"
    val userId = prefs.getString("userId", "") ?: ""
    val users = JSONObject(usersJson)
    val user = if (users.has(userId)) users.optJSONObject(userId) else null

    var name by remember { mutableStateOf("") }
    val dob = user?.optString("dob", "Unknown") ?: "Unknown"
    var newPassword by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf(user?.optString("profileImage", "") ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileImageUri = it.toString()
        }
    }

    LaunchedEffect(Unit) {
        if (user != null) {
            name = user.optString("name", "")
        } else {
            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = primaryBlue) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = white) },
                    label = { Text("Home", color = white) },
                    selected = false,
                    onClick = onNavigateHome
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = white) },
                    label = { Text("Profile", color = white, fontWeight = FontWeight.Bold) },
                    selected = true,
                    onClick = {}
                )
            }
        },
        containerColor = white
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Profile Image
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .shadow(6.dp, CircleShape)
                    .background(Color.LightGray)
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                if (profileImageUri.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUri),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile",
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Hi, $name 👋", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
            Spacer(modifier = Modifier.height(24.dp))

            // Editable Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DOB (not editable)
            OutlinedTextField(
                value = dob,
                onValueChange = {},
                label = { Text("Date of Birth") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Password
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (user != null) {
                        user.put("name", name)
                        if (newPassword.isNotBlank()) {
                            user.put("password", sha256(newPassword)) // Apply hash
                        }
                        user.put("profileImage", profileImageUri)
                        users.put(userId, user)
                        prefs.edit()
                            .putString("users", users.toString())
                            .putString("userId", userId)
                            .apply()
                        Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Changes", color = white, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = {
                    prefs.edit().clear().apply()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Logout", color = white, fontSize = 16.sp)
            }
        }
    }
}
