package com.example.railid

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.alexzhirkevich.customqrgenerator.compose.rememberQrCodePainter

import com.example.railid.nearby.TravellerNearbyHandler

import com.example.railid.utils.generateFingerprintHash
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailsScreen(navController: NavHostController, ticketId: String) {
    val context = LocalContext.current
    val ticket: Ticket? = remember {
        val prefs = context.getSharedPreferences("RailIdPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("bookedTickets", "[]") ?: "[]"
        val type = object : TypeToken<List<Ticket>>() {}.type
        val allTickets: List<Ticket> = Gson().fromJson(json, type)
        mutableStateOf(allTickets.find { it.ticketId == ticketId })
    }.value

    if (ticket == null) {
        Text("Ticket not found.")
        return
    }

    // Start Nearby and listen for verification requests
    LaunchedEffect(Unit) {
        TravellerNearbyHandler.startAdvertising(context) { receivedId ->
            if (receivedId == ticket.ticketId) {
                authenticateFingerprint(
                    context,
                    onSuccess = {
                        val hash = generateFingerprintHash(context)
                        TravellerNearbyHandler.sendVerificationResponse(context, hash)
                        Toast.makeText(context, "Ticket Verified!", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Fingerprint doesn't match!", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "Ticket ID doesn't match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ticket Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Train: ${ticket.trainName} (${ticket.trainNumber})", style = MaterialTheme.typography.titleLarge)
            Text("Date: ${ticket.date}")
            Text("From: ${ticket.source} To: ${ticket.destination}")
            Text("Class: ${ticket.travelClass}")
            Text("Departure: ${ticket.departureTime}")
            Text("Passengers: ${ticket.passengers.size}")
            Text("Seats: ${ticket.seatNumbers.joinToString(", ")}")
            Spacer(modifier = Modifier.height(24.dp))

            val qrData = "${ticket.ticketId}:${ticket.trainNumber}:${ticket.source}:${ticket.destination}"
            val painter = rememberQrCodePainter(qrData)
            Image(
                painter = painter,
                contentDescription = "Ticket QR",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
