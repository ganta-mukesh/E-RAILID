package com.example.railid

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import java.util.concurrent.Executors
import java.net.URLEncoder
import java.net.URLDecoder

@Composable
fun BiometricAuthScreen(
    navController: NavHostController,
    trainJson: String,
    authMethod: AuthMethod
) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val executor = remember { ContextCompat.getMainExecutor(context) }

    LaunchedEffect(Unit) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("${authMethod.name} Verification")
            .setDescription("Authenticate to confirm booking")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    navController.navigate("bookingConfirmed/${URLEncoder.encode(trainJson, "UTF-8")}")
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}