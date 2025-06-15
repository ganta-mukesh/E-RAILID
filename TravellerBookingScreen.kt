package com.example.railid

import android.app.DatePickerDialog
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import java.util.*

@Composable
fun TravellerBookingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    // Form state
    var source by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var travelDate by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("Sleeper") }
    var trains by remember { mutableStateOf(emptyList<Train>()) }
    var showSourceDropdown by remember { mutableStateOf(false) }
    var showDestinationDropdown by remember { mutableStateOf(false) }

    // Station options
    val stationOptions = listOf(
        "Delhi", "Mumbai", "Chennai", "Kolkata", "Bengaluru",
        "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow"
    )

    // Class options
    val classOptions = listOf("Sleeper", "AC", "First Class")

    // Date picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            travelDate = "$day/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = calendar.timeInMillis
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Source Station
        Text("From", style = MaterialTheme.typography.titleSmall)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = source,
                onValueChange = {},
                label = { Text("Select Source") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSourceDropdown = true },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Select Station",
                        modifier = Modifier.clickable { showSourceDropdown = true }
                    )
                },
                readOnly = true
            )
            DropdownMenu(
                expanded = showSourceDropdown,
                onDismissRequest = { showSourceDropdown = false }
            ) {
                stationOptions.forEach { station ->
                    DropdownMenuItem(
                        text = { Text(station) },
                        onClick = {
                            source = station
                            showSourceDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Destination Station
        Text("To", style = MaterialTheme.typography.titleSmall)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = destination,
                onValueChange = {},
                label = { Text("Select Destination") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDestinationDropdown = true },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Select Station",
                        modifier = Modifier.clickable { showDestinationDropdown = true }
                    )
                },
                readOnly = true
            )
            DropdownMenu(
                expanded = showDestinationDropdown,
                onDismissRequest = { showDestinationDropdown = false }
            ) {
                stationOptions.forEach { station ->
                    DropdownMenuItem(
                        text = { Text(station) },
                        onClick = {
                            destination = station
                            showDestinationDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker
        Text("Journey Date", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = travelDate,
            onValueChange = {},
            label = { Text("Select Date") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            trailingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    modifier = Modifier.clickable { datePickerDialog.show() }
                )
            },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Class Selection
        Text("Select Class", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            classOptions.forEach { cls ->
                FilterChip(
                    selected = selectedClass == cls,
                    onClick = { selectedClass = cls },
                    label = { Text(cls) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Button
        Button(
            onClick = {
                trains = getMockTrains(source, destination, travelDate, selectedClass)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = source.isNotEmpty() && destination.isNotEmpty() && travelDate.isNotEmpty()
        ) {
            Text("Search Trains")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Train List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(trains) { train ->
                TrainCard(
                    train = train,
                    onClick = {
                        navController.navigate("passengerDetails/${Uri.encode(Gson().toJson(train))}")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TrainCard(
    train: Train,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(train.name, style = MaterialTheme.typography.titleLarge)
            Text("Train No: ${train.number}", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${train.source} to ${train.destination}")
                Text(train.time)
            }
            Text("Date: ${train.date}")
            Text("Class: ${train.travelClass}")
            Text("Available Seats: ${train.seats}")
            Text(
                "Fare: ₹${"%.2f".format(train.fare)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

private fun getMockTrains(
    source: String,
    destination: String,
    date: String,
    travelClass: String
): List<Train> {
    if (source.isEmpty() || destination.isEmpty() || date.isEmpty()) return emptyList()

    val random = Random()
    val baseFare = when (travelClass) {
        "Sleeper" -> 500.0
        "AC" -> 1000.0
        "First Class" -> 1500.0
        else -> 500.0
    }

    return listOf(
        Train(
            name = "Rajdhani Express",
            number = "12951",
            date = date,
            time = "17:00",
            source = source,
            destination = destination,
            travelClass = travelClass,
            seats = random.nextInt(50) + 10,
            fare = baseFare * 1.5
        ),
        Train(
            name = "Shatabdi Express",
            number = "12001",
            date = date,
            time = "06:00",
            source = source,
            destination = destination,
            travelClass = travelClass,
            seats = random.nextInt(50) + 10,
            fare = baseFare * 1.2
        ),
        Train(
            name = "Jan Shatabdi",
            number = "12055",
            date = date,
            time = "12:30",
            source = source,
            destination = destination,
            travelClass = travelClass,
            seats = random.nextInt(50) + 10,
            fare = baseFare * 0.8
        )
    )
}