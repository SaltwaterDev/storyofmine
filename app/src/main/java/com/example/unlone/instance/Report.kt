package com.example.unlone.instance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Report(
    val type: String? = null,
    val post: Post? = null,
    val reportReason: String? = null,
    val reportedBy: String
): Parcelable