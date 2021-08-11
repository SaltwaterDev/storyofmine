package com.example.unlone.instance

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
        var title: String = "",
        var imagePath: String = "",
        var imageUri: Uri? = null,
        var journal: String = "",
        var uid: String = "",
        var username: String = "",
        var label: String = "",
        var createdTimestamp: String = "",
        var createdDate: String = "",
        var pid: String = "",
        var comment: Boolean = true,
        var save: Boolean = true
) : Parcelable