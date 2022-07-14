package com.unlone.app.domain.entities

data class Comment(
    var cid: String? = null,
    var uid: String? = null,
    var username: String? = null,
    var content: String? = null,
    var timestamp: String? = null,
)
