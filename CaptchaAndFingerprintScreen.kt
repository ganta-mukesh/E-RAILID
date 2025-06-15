@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.railid

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.example.railid.nearby.TravellerNearbyHandler
import com.example.railid.utils.sha256

@Composable
fun CaptchaAndFingerprintScreen(
    navController: NavHostController,
    bookingData: BookingData
) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    var captchaInput by remember { mutableStateOf("") }
    val generatedCaptcha = remember { (1000..9999).random().toString() }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        BookingConfirmation(bookingData, navController)
    } else {
        AuthScreen(
            generatedCaptcha = generatedCaptcha,
            captchaInput = captchaInput,
            onCaptchaChange = { captchaInput = it },
            onAuthClick = {
                authenticateWithFingerprint(activity) {
                    val ticket = createTicketFromBookingData(bookingData, AuthMethod.FINGERPRINT)
                    saveTicket(context, ticket)

                    val ticketId = "${ticket.trainNumber}_${ticket.date}_${ticket.passengers.first().name}"
                    val fingerprintHash = sha256(ticketId + "_my_secret")


                    TravellerNearbyHandler.startAdvertising(context) {
                        TravellerNearbyHandler.sendVerificationResponse(context, fingerprintHash)
                    }

                    showSuccess = true
                }
            }
        )
    }
}

@Composable
private fun AuthScreen(
    generatedCaptcha: String,
    captchaInput: String,
    onCaptchaChange: (String) -> Unit,
    onAuthClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Security Verification", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Enter this code:", style = MaterialTheme.typography.bodyLarge)
        Text(
            text = generatedCaptcha,
            style = MaterialTheme.typography.displayMedium,
            color = Color(0xFF0D47A1)
        )

        OutlinedTextField(
            value = captchaInput,
            onValueChange = onCaptchaChange,
            label = { Text("CAPTCHA") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAuthClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = captchaInput == generatedCaptcha
        ) {
            Text("Verify with Fingerprint")
        }
    }
}

@Composable
private fun BookingConfirmation(
    bookingData: BookingData,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color.Green,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Ticket Booked!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "${'$'}{bookingData.train.source} → ${'$'}{bookingData.train.destination}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text("Date: ${'$'}{bookingData.train.date}")
                Text("Passengers: ${'$'}{bookingData.passengers.size}")
                Text(
                    "Verified with Fingerprint",
                    color = Color(0xFF0D47A1),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("myBookings") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View My Bookings")
        }
    }
}

private fun authenticateWithFingerprint(
    activity: FragmentActivity,
    onSuccess: () -> Unit
) {
    val biometricManager = BiometricManager.from(activity)
    when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Confirm your identity to complete booking")
                .setNegativeButtonText("Cancel")
                .build()

            val biometricPrompt = BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Toast.makeText(activity, "Error: ${'$'}errString", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            biometricPrompt.authenticate(promptInfo)
        }

        else -> Toast.makeText(activity, "Fingerprint auth not available", Toast.LENGTH_LONG).show()
    }
}
