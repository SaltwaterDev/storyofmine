package com.unlone.app.instance

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Comment (    var uid: String? = null,
                        var username: String? = null,
                        var content: String? = null,
                        var timestamp: String? = null,
                        var score: Float = 0f,
                        var cid: String? = null,
                        var referringPid: String? = null
): Parcelable
