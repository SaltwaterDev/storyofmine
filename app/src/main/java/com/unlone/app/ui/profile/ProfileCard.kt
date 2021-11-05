package com.unlone.app.ui.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileCard (var title: String? = null,
                        var backgroundColour: String? = null,
): Parcelable