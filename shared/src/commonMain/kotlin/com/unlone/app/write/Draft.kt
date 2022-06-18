package com.unlone.app.write

import io.realm.kotlin.types.RealmObject

data class Draft(
    val did: String = "",
    val subId: String = "",
    val title: String = "",
    val content: String = "",
    val topics: List<String> = emptyList(),
    val timeStamp: Long = 0L,
){
    companion object{
        fun mock() = Draft(
            "12345",
            "1",
            "draft1",
            "abcde",
        )
    }
}

data class DraftRealmObject(
    val title: String,
    val content: String,
): RealmObject