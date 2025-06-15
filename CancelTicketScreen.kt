package com.example.railid

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// --- SHARED PREF HELPERS ---
private const val PREFS_NAME = "railid_tickets"
private const val TICKETS_KEY = "all_tickets"

fun getTicketsList(context: Context): MutableList<Ticket> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = prefs.getString(TICKETS_KEY, "[]") ?: "[]"
    val type = object : TypeToken<MutableList<Ticket>>() {}.type
    return Gson().fromJson(json, type)
}

fun saveTicketsList(context: Context, tickets: List<Ticket>) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(TICKETS_KEY, Gson().toJson(tickets)).apply()
}

// --- CANCEL TICKET SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelTicketScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedTicket by remember { mutableStateOf<Ticket?>(null) }
    var showCaptcha by remember { mutableStateOf(false) }
    val activeTickets = remember { mutableStateListOf<Ticket>() }

    val backgroundBlue = Color(0xFF001F3F)
    val white = Color.White

    LaunchedEffect(Unit) {
        activeTickets.clear()
        activeTickets.addAll(getTicketsList(context).filter { it.status == TicketStatus.ACTIVE })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cancel Tickets", color = white) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = backgroundBlue
                )
            )
        },
        containerColor = backgroundBlue
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (!showCaptcha) {
                if (activeTickets.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No active tickets to cancel.", color = white)
                    }
                } else {
                    LazyColumn {
                        items(activeTickets.reversed()) { ticket ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        selectedTicket = ticket
                                        showCaptcha = true
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = white),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Train: ${ticket.trainName} (${ticket.trainNumber})", color = backgroundBlue)
                                    Text("Route: ${ticket.source} → ${ticket.destination}", color = backgroundBlue)
                                    Text("Seats: ${ticket.seatNumbers.joinToString()}", color = backgroundBlue)
                                }
                            }
                        }
                    }
                }
            } else {
                CaptchaPrompt(
                    ticket = selectedTicket!!,
                    onCancelConfirmed = {
                        val updated = getTicketsList(context).map {
                            if (it.ticketId == selectedTicket!!.ticketId)
                                it.copy(status = TicketStatus.CANCELLED)
                            else it
                        }
                        saveTicketsList(context, updated)
                        activeTickets.clear()
                        activeTickets.addAll(updated.filter { it.status == TicketStatus.ACTIVE })
                        showCaptcha = false
                        selectedTicket = null
                        navController.popBackStack()
                    },
                    onCancel = {
                        showCaptcha = false
                        selectedTicket = null
                    },
                    backgroundBlue = backgroundBlue
                )
            }
        }
    }
}

@Composable
fun CaptchaPrompt(
    ticket: Ticket,
    onCancelConfirmed: () -> Unit,
    onCancel: () -> Unit,
    backgroundBlue: Color
) {
    val captcha = remember { (1000..9999).random().toString() }
    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text("To cancel the ticket, complete CAPTCHA:", color = Color.White)
        Spacer(Modifier.height(12.dp))
        Text("Enter CAPTCHA: $captcha", color = Color.White)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("CAPTCHA") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.LightGray,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.LightGray
            )

        )
        Spacer(Modifier.height(16.dp))
        Row {
            Button(
                onClick = {
                    if (userInput == captcha) onCancelConfirmed()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cancel Ticket", color = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onCancel, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                Text("Back")
            }
        }
    }
}
