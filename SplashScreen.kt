package com.example.railid

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val scale = remember { Animatable(0f) }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("RailIdPrefs", Context.MODE_PRIVATE)
    val userId = prefs.getString("userId", null)

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
        delay(1500)

        val destination = when {
            userId == null -> "login"
            userId.startsWith("tte_") -> "tteHome"
            else -> "travellerHome"
        }

        navController.navigate(destination) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.railid_logo),
            contentDescription = "RailId Logo",
            modifier = Modifier
                .size(200.dp)
                .scale(scale.value)
                .clip(CircleShape)
        )
    }
}
