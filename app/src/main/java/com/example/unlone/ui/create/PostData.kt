package com.example.unlone.ui.create

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

@Parcelize
/* This class used to contain the data during creating the post
 */
data class PostData(
        var title: String = "",
        var imageUri: Uri? = null,
        var journal: String = "",
        var uid: String = "",
        var labels: ArrayList<String> = ArrayList<String>(),
        var comment: Boolean = true,
        var save: Boolean = true
) : Parcelable