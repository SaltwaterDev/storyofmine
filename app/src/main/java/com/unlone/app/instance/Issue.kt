package com.unlone.app.instance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Issue(
    var issueType: String = "",
    var detail: String = "",
    var uid: String = ""

): Parcelable