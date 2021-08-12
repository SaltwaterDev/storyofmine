package com.example.unlone.instance

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

@Parcelize
data class PostData(
        var title: String = "",
        var imageUri: Uri? = null,
        var journal: String = "",
        var uid: String = "",
        var labels: ArrayList<String> = ArrayList<String>(),
) : Parcelable