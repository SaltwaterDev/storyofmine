@file:JvmMultifileClass
@file:JvmName("Utils")

package com.example.unlone.utils

import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


fun convertTimeStamp(timestamp: String, dateFormat: String = "HH:mm   dd'th' MMM"): String {
    return try {
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        val netDate = Date(timestamp.toLong() * 1000)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}