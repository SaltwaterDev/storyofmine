package com.unlone.app.model

import androidx.annotation.Keep


@Keep
data class SubComment (var uid: String? = null,
                       var username: String? = null,
                       var content: String? = null,
                       var timestamp: String? = null,
                       var score: Float = 0f,
                       var cid: String? = null,
                       var parent_cid: String? = null,
                       var parent_pid: String? = null
)
