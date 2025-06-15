package com.example.railid

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import com.example.railid.utils.sha256



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isTraveller by remember { mutableStateOf(true) }
    var isSignUp by remember { mutableStateOf(false) }

    // Traveller signup/signin fields
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") } // DOB string
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // TTE login fields
    var tteUser by remember { mutableStateOf("") }
    var ttePass by remember { mutableStateOf("") }

    val prefs = context.getSharedPreferences("RailIdPrefs", Context.MODE_PRIVATE)

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val formatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(GregorianCalendar(year, month, dayOfMonth).time)
            dob = formatted
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(50.dp))

        Text(
            text = "RailId Login",
            color = Color(0xFF0D47A1),
            fontSize = 28.sp
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.LightGray)
                .padding(6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { isTraveller = true; isSignUp = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTraveller) Color(0xFF0D47A1) else Color.Gray
                )
            ) { Text("Traveller", color = Color.White) }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = { isTraveller = false; isSignUp = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isTraveller) Color(0xFF0D47A1) else Color.Gray
                )
            ) { Text("TTE", color = Color.White) }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (isTraveller) {
                    if (isSignUp) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = number, onValueChange = { number = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

                        OutlinedTextField(
                            value = dob,
                            onValueChange = {},
                            label = { Text("Date of Birth") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledContainerColor = Color.Transparent,
                                disabledLabelColor = Color.Gray
                            )
                        )

                        OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                when {
                                    name.isBlank() || number.isBlank() || email.isBlank() || dob.isBlank() ||
                                            userId.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                        Toast.makeText(context, "All fields required", Toast.LENGTH_SHORT).show()
                                    }
                                    password != confirmPassword -> {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        val usersJson = prefs.getString("users", "{}")
                                        val users = JSONObject(usersJson)

                                        if (users.has(userId)) {
                                            Toast.makeText(context, "User ID already exists", Toast.LENGTH_SHORT).show()
                                        } else {
                                            val hashedPassword = sha256(password)
                                            val newUser = JSONObject().apply {
                                                put("name", name)
                                                put("number", number)
                                                put("email", email)
                                                put("dob", dob)
                                                put("password", hashedPassword)
                                            }
                                            users.put(userId, newUser)
                                            prefs.edit().putString("users", users.toString()).apply()
                                            Toast.makeText(context, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                                            isSignUp = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                        ) {
                            Text("Sign Up", color = Color.White)
                        }
                    } else {
                        OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val usersJson = prefs.getString("users", "{}")
                                val users = JSONObject(usersJson)

                                if (users.has(userId)) {
                                    val user = users.getJSONObject(userId)
                                    val storedHash = user.getString("password")
                                    if (storedHash == sha256(password)) {
                                        prefs.edit().putString("userId", userId).apply()
                                        navController.navigate("travellerHome")
                                    } else {
                                        Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                        ) {
                            Text("Login", color = Color.White)
                        }

                        TextButton(
                            onClick = { isSignUp = true },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Don't have an account? Sign Up", color = Color.Gray)
                        }
                    }
                } else {
                    OutlinedTextField(value = tteUser, onValueChange = { tteUser = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = ttePass, onValueChange = { ttePass = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (tteUser == "tte1" && ttePass == "tte@123") {
                                navController.navigate("tteHome")
                            } else {
                                Toast.makeText(context, "Invalid TTE credentials", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                    ) {
                        Text("Login", color = Color.White)
                    }
                }
            }
        }
    }
}
