package com.example.unlone.instance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment (    var uid: String? = null,
                        var username: String? = null,
                        var content: String? = null,
                        var timestamp: String? = null,
                        var score: Float = 0f,
                        var liked: Boolean = false,
                        var cid: String? = null,
                        ): Parcelable