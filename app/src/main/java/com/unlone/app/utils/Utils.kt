@file:JvmMultifileClass
@file:JvmName("Utils")

package com.unlone.app.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


@RequiresApi(Build.VERSION_CODES.N)
fun convertTimeStamp(timestamp: String, language: String?): String {
    return try {
        lateinit var sdf: SimpleDateFormat
        lateinit var netDate: Date
        when (language){
            "zh" -> {
                sdf = SimpleDateFormat("MMMdd'日'  |   HH:mm", Locale.getDefault())
                netDate = Date(timestamp.toLong() * 1000)
            }
            "COMMENT" -> {
                sdf = SimpleDateFormat("dd-MM-yyyy   H:mm", Locale.getDefault())
                netDate = Date(timestamp.toLong())
            }
            else -> {
                SimpleDateFormat("HH:mm   dd'th' MMM", Locale.getDefault())
                netDate = Date(timestamp.toLong() * 1000)
            }
        }
        sdf.timeZone = TimeZone.getDefault()
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
