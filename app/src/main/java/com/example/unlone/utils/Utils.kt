@file:JvmMultifileClass
@file:JvmName("Utils")

package com.example.unlone.utils

import android.content.Context
import android.util.DisplayMetrics
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

fun dpConvertPx(dp: Int, context: Context): Int {
    val metrics: DisplayMetrics = context.resources.displayMetrics
    return dp * metrics.densityDpi / 160
}

fun getImageHorizontalMargin(ratio: Float, context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    val deviceWidth = displayMetrics.widthPixels.toFloat()
    val slope = deviceWidth / 6 - deviceWidth / 4 * 45 / 44
    val margin = slope * (ratio - 4 / 5f) + deviceWidth / 4
    return margin.toInt()
}
