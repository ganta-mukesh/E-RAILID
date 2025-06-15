package com.example.railid

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val tickets = remember {
        TicketManager.getActiveTickets(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (tickets.isEmpty()) {
            EmptyBookingsState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tickets) { ticket ->
                    TicketItem(ticket = ticket, navController = navController)
                }
            }
        }
    }
}

@Composable
fun TicketItem(ticket: Ticket, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with train info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${ticket.trainName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Train No: ${ticket.trainNumber}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = ticket.date,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Route information
            Text(
                text = "${ticket.source} → ${ticket.destination}",
                style = MaterialTheme.typography.titleMedium
            )

            // Class and departure time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Class: ${ticket.travelClass}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Dep: ${ticket.departureTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // QR Code (small preview)
            TicketQrCode(
                ticket = ticket,
                size = 100,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Passengers and seats
            Text(
                text = "Passengers: ${ticket.passengers.size}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Seats: ${ticket.seatNumbers.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall
            )

            // View Details button
            Button(
                onClick = {
                    navController.navigate("ticketDetails/${ticket.ticketId}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("View Ticket Details")
            }
        }
    }
}

@Composable
fun EmptyBookingsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Upcoming Journeys",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Your future bookings will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

object TicketManager {
    private const val PREFS_NAME = "railid_tickets"
    private const val TICKETS_KEY = "all_tickets"

    fun getActiveTickets(context: Context): List<Ticket> {
        val allTickets = getAllTickets(context)
        return allTickets
            .filter { it.status == TicketStatus.ACTIVE }
            .sortedBy { it.date }
    }

    private fun getAllTickets(context: Context): List<Ticket> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(TICKETS_KEY, "[]") ?: "[]"
        val type = object : TypeToken<List<Ticket>>() {}.type
        return Gson().fromJson(json, type)
    }

    // ✅ ✅ Add this below the existing functions
    fun getTicketById(context: Context, ticketId: String): Ticket? {
        return getAllTickets(context).find { it.ticketId == ticketId }
    }
}
@Composable
fun TicketQrCode(ticket: Ticket, size: Int, modifier: Modifier = Modifier) {
    val qrBitmap = remember { generateQrCode("RAILID-${ticket.ticketId}", size) }

    qrBitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Ticket QR Code",
            modifier = modifier.size(size.dp)
        )
    }
}

private fun generateQrCode(data: String, size: Int): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (bitMatrix.get(x, y)) android.graphics.Color.BLACK
                else android.graphics.Color.WHITE
            }
        }

        android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    } catch (e: Exception) {
        null
    }
}