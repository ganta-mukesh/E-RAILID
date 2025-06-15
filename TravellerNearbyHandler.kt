package com.example.railid.nearby

import android.content.Context
import android.widget.Toast
import com.google.android.gms.nearby.Nearby

import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.Strategy

object TravellerNearbyHandler {

    private const val SERVICE_ID = "railid_verification_service"
    private var currentEndpointId: String? = null
    private lateinit var onVerifyRequest: (String) -> Unit

    fun startAdvertising(context: Context, onVerificationRequested: (String) -> Unit) {
        this.onVerifyRequest = onVerificationRequested

        val strategy = Strategy.P2P_CLUSTER
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()

        Nearby.getConnectionsClient(context)
            .startAdvertising(
                "TravellerDevice",
                SERVICE_ID,
                connectionLifecycleCallback(context),
                advertisingOptions
            )
            .addOnSuccessListener {
                Toast.makeText(context, "Advertising Started", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to advertise", Toast.LENGTH_SHORT).show()
            }
    }

    private fun connectionLifecycleCallback(context: Context) = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback(context))
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                currentEndpointId = endpointId
            }
        }

        override fun onDisconnected(endpointId: String) {}
    }

    private fun payloadCallback(context: Context) = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val message = payload.asBytes()?.toString(Charsets.UTF_8) ?: return
            if (message.startsWith("VERIFY_REQUEST:")) {
                val ticketId = message.removePrefix("VERIFY_REQUEST:")
                currentEndpointId = endpointId
                onVerifyRequest(ticketId)
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    fun sendVerificationResponse(context: Context, hash: String) {
        currentEndpointId?.let {
            val payload = Payload.fromBytes("VERIFIED:$hash".toByteArray())
            Nearby.getConnectionsClient(context).sendPayload(it, payload)
        }
    }
}
