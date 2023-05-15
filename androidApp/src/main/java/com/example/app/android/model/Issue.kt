package com.example.app.model

import androidx.annotation.Keep

@Keep
data class Issue(
    var issueType: String = "",
    var detail: String = "",
    var uid: String = ""

)