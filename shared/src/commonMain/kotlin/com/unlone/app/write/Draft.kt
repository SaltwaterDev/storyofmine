package com.unlone.app.write

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

data class Draft(
    val id: Long = 0L,
    val version: Long = 0L,
    val title: String = "",
    val content: String = "",
    val topics: List<String> = emptyList(),
    val timeStamp: Long = 0L,
) {
    companion object {
        fun mock() = Draft(
            12345,
            13579,
            "draft1",
            "abcde",
        )
    }
}

class ParentDraftRealmObject : RealmObject {
    var id: Long = randomID()
    lateinit var title: String
    lateinit var childDrafts: RealmList<ChildDraft>
    var topics: List<String> = emptyList()
}

fun ParentDraftRealmObject.latestDraft(): ChildDraft {
    return childDrafts.maxByOrNull { it.timeStamp } ?: childDrafts.first()
}

class ChildDraft: RealmObject{
    var version: Long = randomID()
    lateinit var content: String
    var timeStamp: RealmInstant = RealmInstant.from(100, 1000)
}


// MUST BE "class", not "data class"
class DraftRealmObject : RealmObject {
    var id: Long = randomID()
    var version: Long = randomID()
    var title: String = ""
    var content: String = ""
    var topics: List<String> = emptyList()
    var timeStamp: RealmInstant = RealmInstant.from(100, 1000)
}

private fun randomID(): Long = List(16) {
    (0..9).random()
}.joinToString("").toLong()