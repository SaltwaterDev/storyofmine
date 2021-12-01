package com.unlone.app.instance

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class ProfileCard (var title: String? = null,
                        var backgroundColour: String? = null,
): Parcelable