package com.unlone.app.model

import androidx.annotation.Keep

@Keep
data class ProfileCard(
    var title: String? = null,
    var backgroundColour: String? = null,
)