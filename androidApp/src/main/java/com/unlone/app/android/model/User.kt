package com.unlone.app.model

import androidx.annotation.Keep

@Keep
data class User (
    var uid: String? = null,
    var username: String? = null,
    var personae: ArrayList<String>? = null,
    var persona: String? = null,
    var bio: String? = null,
    var followingCategories: List<String>? = null,
    var interests: List<String>? = null,
    var notificationToken: String? = null,
    var identity:String? = null
)