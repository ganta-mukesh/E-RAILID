package com.example.railid

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.railid.tte.*

import org.json.JSONObject

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var userName by remember { mutableStateOf("Traveller") }

    fun updateUserName() {
        val prefs = context.getSharedPreferences("RailIdPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("userId", "") ?: ""
        val usersJson = prefs.getString("users", "{}") ?: "{}"
        val users = JSONObject(usersJson)
        val user = users.optJSONObject(userId)
        userName = user?.getString("name") ?: "Traveller"
    }

    LaunchedEffect(Unit) {
        updateUserName()
    }

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") { SplashScreen(navController) }

        composable("login") { LoginScreen(navController) }

        composable("travellerHome") {
            TravellerHomeScreen(
                userName = userName,
                onNavigateProfile = { navController.navigate("profile") },
                onNavigateToScreen = { route ->
                    when (route) {
                        "booking" -> navController.navigate("booking")
                        "cancelTicket" -> navController.navigate("cancelTicket")
                        "myBookings" -> navController.navigate("myBookings")
                        "bookingHistory" -> navController.navigate("bookingHistory")
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateHome = {
                    updateUserName()
                    navController.navigate("travellerHome")
                },
                onLogout = {
                    val prefs = context.getSharedPreferences("RailIdPrefs", Context.MODE_PRIVATE)
                    prefs.edit().remove("userId").apply()
                    navController.navigate("login") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("booking") {
            TravellerBookingScreen(navController)
        }

        composable(
            "passengerDetails/{trainJson}",
            arguments = listOf(navArgument("trainJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val trainJson = backStackEntry.arguments?.getString("trainJson") ?: ""
            PassengerDetailsScreen(navController = navController, trainJson = trainJson)
        }

        composable("captchaAuth") {
            val bookingData = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<BookingData>("bookingData")

            if (bookingData != null) {
                CaptchaAndFingerprintScreen(navController = navController, bookingData = bookingData)
            } else {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Booking data not found.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }
        }

        composable("myBookings") { MyBookingsScreen(navController) }

        composable("cancelTicket") { CancelTicketScreen(navController) }

        composable("bookingHistory") { BookingHistoryScreen(navController) }

        composable("tteHome") {
            TTEHomeScreen(
                userName = "TTE Officer",
                onNavigateProfile = { navController.navigate("tteProfile") },
                onNavigateToScreen = { route ->
                    when (route) {
                        "allTickets" -> navController.navigate("tte_all_tickets")
                        "verifyTicket" -> navController.navigate("verifyTicket")
                    }
                }
            )
        }

        composable("tteProfile") {
            TTEProfileScreen(
                navController = navController,
                onNavigateHome = { navController.navigate("tteHome") }
            )
        }

        composable("tte_all_tickets") {
            AllTicketsScreen(navController)
        }

        composable("verifyTicket") {
            VerifyTicketScreen(navController)
        }

        composable("ticketDetails/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
            TicketDetailsScreen(navController, ticketId)
        }
    }
}
