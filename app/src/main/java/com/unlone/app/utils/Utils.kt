@file:JvmMultifileClass
@file:JvmName("Utils")

package com.unlone.app.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


fun convertTimeStamp(timestamp: String, language: String?): String {
    return try {
        lateinit var sdf: SimpleDateFormat
        lateinit var netDate: Date
        when (language){
            "zh" -> {
                sdf = SimpleDateFormat("MMMdd'日'   |   HH:mm", Locale.getDefault())
                netDate = Date(timestamp.toLong())
            }
            "COMMENT" -> {
                sdf = SimpleDateFormat("dd-MM-yyyy   H:mm", Locale.US)
                netDate = Date(timestamp.toLong())
            }
            else -> {
                sdf = SimpleDateFormat("HH:mm   dd'th' MMM", Locale.US)
                netDate = Date(timestamp.toLong())
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

fun getHeight(context: Context, textView: TextView): Int {
    val displayMetrics = context.resources.displayMetrics
    // val deviceHeight = displayMetrics.heightPixels
    val deviceWidth = displayMetrics.widthPixels
    val widthMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
    val heightMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    textView.measure(widthMeasureSpec, heightMeasureSpec)
    return textView.measuredHeight
}
