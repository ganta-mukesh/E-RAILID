package com.example.railid.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import android.graphics.Bitmap
import androidx.compose.foundation.layout.size
import com.example.railid.Ticket

@Composable
fun TicketQrCode(ticket: Ticket, size: Int = 200) {
    val qrData = ticket.ticketId
    val bitmap = generateQRCode(qrData, size)

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Ticket QR",
            modifier = Modifier.size(size.dp),
            contentScale = ContentScale.FillBounds
        )
    }
}

fun generateQRCode(text: String, size: Int): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.Black.toArgb() else Color.White.toArgb())
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}
