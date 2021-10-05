package com.example.unlone.instance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
data class User (    var uid: String? = null,
                     var username: String? = null,
                     var personae: ArrayList<String>? = null,
                     var persona: String? = null,
                     var bio: String? = null,
                     var followingCategories: ArrayList<String>? = null,
): Parcelable