package com.unlone.app.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Issue(
    var issueType: String = "",
    var detail: String = "",
    var uid: String = ""

): Parcelable