package com.example.railid.utils

import android.content.Context
import com.example.railid.Ticket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TicketManager {
    private const val PREF_NAME = "railid_tickets"
    private const val KEY_TICKETS = "booked_tickets"

    fun getTicketById(context: Context, ticketId: String): Ticket? {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val ticketJson = sharedPrefs.getString(KEY_TICKETS, null) ?: return null
        val type = object : TypeToken<List<Ticket>>() {}.type
        val allTickets: List<Ticket> = Gson().fromJson(ticketJson, type)
        return allTickets.find { it.ticketId == ticketId }
    }
}
