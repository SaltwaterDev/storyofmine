package com.example.unlone.instance

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class Comment {
    var cid: String? = null
    var author_uid: String? = null
    var author_username: String? = null
    var content: String? = null
    var timestamp: String
    var score = 0f

    constructor() {
        timestamp = LocalDateTime.now().toString()
    }

    constructor(author_uid: String?, author_username: String?, content: String?) {
        this.author_uid = author_uid
        this.author_username = author_username
        this.content = content
        timestamp = LocalDateTime.now().toString()
    }

    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["cid"] = cid
        result["author_uid"] = author_uid
        result["author_username"] = author_username
        result["content"] = content
        result["timestamp"] = timestamp
        return result
    }
}