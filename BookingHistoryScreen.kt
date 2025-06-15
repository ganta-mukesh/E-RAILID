package com.example.railid

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


data class HistoryTicket(
    val passengerName: String,
    val source: String,
    val destination: String,
    val date: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val allTickets = remember { mutableStateListOf<Ticket>() }

    LaunchedEffect(Unit) {
        val tickets = getTicketsList(context)
        allTickets.clear()
        allTickets.addAll(tickets)
    }

    val backgroundBlue = Color(0xFF001F3F)
    val white = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBlue)
            .padding(16.dp)
    ) {
        Text(
            text = "Booking History",
            color = white,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(allTickets) { ticket ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (ticket.status == TicketStatus.CANCELLED) Color(0xFFB71C1C) else white
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Train: ${ticket.trainName} (${ticket.trainNumber})")
                        Text("From ${ticket.source} to ${ticket.destination}")
                        Text("Seats: ${ticket.seatNumbers.joinToString()}")
                        Text(
                            "Status: ${ticket.status}",
                            color = if (ticket.status == TicketStatus.CANCELLED) Color.Red else Color.Green
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TicketHistoryCard(ticket: HistoryTicket) {
    val statusColor = when (ticket.status.uppercase()) {
        "CANCELLED" -> Color(0xFFE74C3C)
        else -> Color(0xFF2ECC71)
    }

    val cardColor = Color.White
    val textColor = Color(0xFF001F3F)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Passenger: ${ticket.passengerName}", fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(ticket.source, fontWeight = FontWeight.Medium, color = textColor)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = textColor)
                Spacer(modifier = Modifier.width(6.dp))
                Text(ticket.destination, fontWeight = FontWeight.Medium, color = textColor)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Date: ${ticket.date}", color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Status: ${ticket.status}",
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }
    }
}
