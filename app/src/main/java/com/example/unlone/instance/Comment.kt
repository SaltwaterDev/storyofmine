package com.example.unlone.instance

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.*
@Parcelize
data class Comment (    var author_uid: String? = null,
                        var author_username: String? = null,
                        var content: String? = null,
                        var timestamp: String? = null,
                        var score: Float = 0f
): Parcelable