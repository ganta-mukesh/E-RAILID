package com.example.railid

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import java.io.Serializable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun PassengerDetailsScreen(navController: NavHostController, trainJson: String) {
    val context = LocalContext.current
    val train = remember { Gson().fromJson(trainJson, Train::class.java) }
    var passengerCount by remember { mutableStateOf(1) }
    val passengers = remember { mutableStateListOf<PassengerInput>().apply {
        add(PassengerInput("", ""))
    }}

    Column(Modifier.padding(16.dp)) {
        Text("Passenger Details", style = MaterialTheme.typography.titleLarge)

        // Passenger counter stepper
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = {
                    if (passengerCount > 1) {
                        passengerCount--
                        passengers.removeAt(passengers.lastIndex)
                    }
                },
                enabled = passengerCount > 1
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }

            Text(
                text = "$passengerCount",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            IconButton(
                onClick = {
                    if (passengerCount < 6) {
                        passengerCount++
                        passengers.add(PassengerInput("", ""))
                    }
                },
                enabled = passengerCount < 6
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }

        // Passenger input fields
        passengers.forEachIndexed { index, passenger ->
            PassengerInputField(
                passenger = passenger,
                onNameChange = { passengers[index] = passenger.copy(name = it) },
                onAgeChange = { passengers[index] = passenger.copy(age = it) },
                number = index + 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continue button
        Button(
            onClick = {
                val validPassengers = passengers.mapNotNull {
                    if (it.name.isNotBlank() && it.age.isNotBlank()) {
                        Passenger(it.name, it.age.toIntOrNull() ?: 0)
                    } else null
                }

                if (validPassengers.size == passengerCount) {
                    // Create booking data with correct parameters
                    val bookingData = BookingData(
                        train = train,
                        passengers = validPassengers,
                        selectedAuthMethod = AuthMethod.NONE
                    ).withGeneratedSeats()

                    // Save booking data to navigate with it
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "bookingData",
                        bookingData
                    )

                    // Navigate to captcha and biometric screen
                    navController.navigate("captchaAuth")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
        ) {
            Text("Continue to Security Check", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun PassengerInputField(
    passenger: PassengerInput,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    number: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Passenger $number",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = passenger.name,
                onValueChange = onNameChange,
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = passenger.age,
                onValueChange = { newAge ->
                    if (newAge.isEmpty() || newAge.toIntOrNull() != null) {
                        onAgeChange(newAge)
                    }
                },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

// Data classes
data class PassengerInput(
    val name: String,
    val age: String
) : Serializable


enum class AuthMethod {
    FINGERPRINT,
    FACE,
    NONE
}