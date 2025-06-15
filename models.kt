package com.example.railid

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

data class Train(
    val name: String,
    val number: String,
    val time: String,
    val date: String,
    val source: String,
    val destination: String,
    val travelClass: String,
    val seats: Int,
    val fare: Double
) : Serializable

data class Passenger(
    val name: String,
    val age: Int,
    val gender: String = "O"
) : Serializable {
    init {
        require(age in 1..120)
        require(gender in listOf("M", "F", "O"))
    }
}

data class BookingData(
    val train: Train,
    val passengers: List<Passenger>,
    val selectedAuthMethod: AuthMethod,
    val seatNumbers: List<String> = emptyList(),
    val totalFare: Double = train.fare * passengers.size
) : Serializable {
    init {
        require(passengers.isNotEmpty())
        require(passengers.size <= 6)
    }

    fun withGeneratedSeats(): BookingData {
        return if (seatNumbers.isEmpty()) {
            val generatedSeats = passengers.mapIndexed { index, _ ->
                "${train.travelClass}-${index + 1}"
            }
            this.copy(seatNumbers = generatedSeats)
        } else {
            require(seatNumbers.size == passengers.size)
            this
        }
    }
}

data class Ticket(
    val ticketId: String = "TKT-${System.currentTimeMillis()}",
    val trainName: String,
    val trainNumber: String,
    val source: String,
    val destination: String,
    val date: String,
    val departureTime: String,
    val arrivalTime: String,
    val travelClass: String,
    val passengers: List<Passenger>,
    val seatNumbers: List<String>,
    val authMethod: AuthMethod,
    val biometricHash: String,
    val totalFare: Double,
    val fare: Double,
    val qrCodeData: String = "RAILID-${UUID.randomUUID().toString().take(8).uppercase()}",
    var status: TicketStatus = TicketStatus.ACTIVE,
    val bookingDate: Long = System.currentTimeMillis()
) : Serializable {
    companion object {
        private const val MAX_SEATS_PER_BOOKING = 6
    }

    init {
        require(passengers.isNotEmpty())
        require(passengers.size <= MAX_SEATS_PER_BOOKING)
        require(seatNumbers.size == passengers.size)
    }

    fun isFutureTicket(): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val ticketDateTime = dateFormat.parse("$date $departureTime")
            ticketDateTime.after(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun formattedDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            outputFormat.format(inputFormat.parse(date))
        } catch (e: Exception) {
            date
        }
    }

    fun formattedTime(): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            outputFormat.format(inputFormat.parse(departureTime))
        } catch (e: Exception) {
            departureTime
        }
    }

    fun getJourneyDuration(): String {
        return try {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            val departure = format.parse(departureTime)
            val arrival = format.parse(arrivalTime)

            val diff = arrival.time - departure.time
            val hours = diff / (60 * 60 * 1000)
            val minutes = (diff / (60 * 1000)) % 60

            "${hours}h ${minutes}m"
        } catch (e: Exception) {
            "N/A"
        }
    }
}

enum class TicketStatus {
    ACTIVE, CANCELLED, COMPLETED, EXPIRED
}

private const val PREFS_NAME = "railid_tickets"
private const val TICKETS_KEY = "all_tickets"

fun saveTicket(context: Context, ticket: Ticket) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val tickets = getTicketsList(prefs).toMutableList()
    tickets.add(ticket)
    prefs.edit().putString(TICKETS_KEY, Gson().toJson(tickets)).apply()
}

fun getTicketsList(prefs: SharedPreferences): List<Ticket> {
    val json = prefs.getString(TICKETS_KEY, "[]") ?: "[]"
    val type = object : TypeToken<List<Ticket>>() {}.type
    return Gson().fromJson(json, type) ?: emptyList()
}

fun getActiveTickets(context: Context): List<Ticket> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return getTicketsList(prefs).filter { it.status == TicketStatus.ACTIVE }
}

fun createTicketFromBookingData(bookingData: BookingData, authMethod: AuthMethod): Ticket {
    return Ticket(
        trainName = bookingData.train.name,
        trainNumber = bookingData.train.number,
        source = bookingData.train.source,
        destination = bookingData.train.destination,
        date = bookingData.train.date,
        departureTime = bookingData.train.time,
        arrivalTime = calculateArrivalTime(bookingData.train.time, bookingData.train.source, bookingData.train.destination),
        travelClass = bookingData.train.travelClass,
        passengers = bookingData.passengers,
        seatNumbers = bookingData.seatNumbers,
        authMethod = authMethod,
        biometricHash = bookingData.passengers.generateBiometricHash(),
        totalFare = bookingData.totalFare,
        fare = bookingData.train.fare
    )
}

fun List<Passenger>.generateBiometricHash(): String {
    val uniqueData = this.sortedBy { it.name }
        .joinToString("|") { "${it.name.lowercase()}:${it.age}:${it.gender}" }
    return try {
        MessageDigest.getInstance("SHA-256")
            .digest(uniqueData.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        ""
    }
}

private fun calculateArrivalTime(departureTime: String, source: String, destination: String): String {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    val time = format.parse(departureTime) ?: return "00:00"

    val durationHours = when {
        source.contains("Delhi") && destination.contains("Mumbai") -> 16
        source.contains("Chennai") && destination.contains("Bangalore") -> 5
        else -> 8
    }

    val calendar = Calendar.getInstance()
    calendar.time = time
    calendar.add(Calendar.HOUR, durationHours)

    return format.format(calendar.time)
}
