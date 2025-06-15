package com.example.railid.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.railid.Passenger
import com.example.railid.Ticket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.security.MessageDigest

fun sha256(input: String): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(input.toByteArray())
        .joinToString("") { "%02x".format(it) }
}

fun hashFingerprintData(passengers: List<Passenger>): String {
    val data = passengers.joinToString { "${it.name}${it.age}" }
    return sha256(data)
}

fun getTicketsList(prefs: SharedPreferences): List<Ticket> {
    return try {
        val json = prefs.getString("all_tickets", "[]") ?: "[]"
        val type = object : TypeToken<List<Ticket>>() {}.type
        Gson().fromJson(json, type) ?: emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
fun generateFingerprintHash(context: Context): String {
    val biometricData = "some-unique-fingerprint-identifier"
    return biometricData.hashCode().toString()
}
object HashUtils {
    fun generateFingerprintHash(context: Context): String {
        val dummyData =
            "unique_fingerprint_key" // Replace with real fingerprint-derived key if needed
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(dummyData.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}