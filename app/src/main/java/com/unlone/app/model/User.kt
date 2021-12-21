package com.unlone.app.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Keep
data class User (
    var uid: String? = null,
    var username: String? = null,
    var personae: ArrayList<String>? = null,
    var persona: String? = null,
    var bio: String? = null,
    var followingCategories: ArrayList<String>? = null,
    var notificationToken: String? = null,
    var identity:String? = null
): Parcelable