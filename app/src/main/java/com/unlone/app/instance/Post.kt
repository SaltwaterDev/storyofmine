package com.unlone.app.instance

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

@Parcelize
@Keep
data class Post(
        var title: String = "",
        var imagePath: String = "",
        var journal: String = "",
        var author_uid: String = "",
        var username: String = "",
        var labels: ArrayList<String> = ArrayList<String>(),
        var category:String = "",
        var createdTimestamp: String = "",
        var createdDate: String = "",
        var pid: String = "",
        var comment: Boolean = true,
        var save: Boolean = true,
) : Parcelable