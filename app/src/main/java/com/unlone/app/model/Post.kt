package com.unlone.app.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Post(
        var title: String = "",
        var imagePath: String = "",
        var journal: String = "",
        var author_uid: String = "",
        var labels: List<String> = emptyList(),
        var category:String = "",
        var createdTimestamp: String = "",
        var pid: String = "",
        var publish: Boolean = false,
        var comment: Boolean = false,
        var save: Boolean = false,
) : Parcelable