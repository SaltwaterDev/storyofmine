package com.unlone.app.write

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


// entities
data class ParentDraft(
    val id: String,
    val childDrafts: List<ChildDraft>,
    val topics: List<String> = emptyList(),
)

data class ChildDraft(
    val version: String,
    val title: String,
    val content: String,
    val timeStamp: Long
)


// classes for realm
class ParentDraftRealmObject : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId.create()
    var childDraftRealmObjects: RealmList<ChildDraftRealmObject> = realmListOf()
    var topics: List<String> = emptyList()
}

fun ParentDraftRealmObject.latestDraft(): ChildDraftRealmObject {
    return childDraftRealmObjects.maxByOrNull { it.timeStamp } ?: childDraftRealmObjects.first()
}

fun ParentDraftRealmObject.toParentDraft() =
    ParentDraft(
        id = this.id.toString(),
        childDrafts = this.childDraftRealmObjects
            .toList()
            .map { it1 -> it1.toChildDraft() },
        topics = this.topics,
    )


class ChildDraftRealmObject : RealmObject {
    var id: ObjectId = ObjectId.create()
    var title: String = ""
    var content: String = ""
    var timeStamp: RealmInstant = RealmInstant.from(100, 1000)
}

fun ChildDraftRealmObject.toChildDraft() =
    ChildDraft(
        this.id.toString(),
        this.title,
        this.content,
        this.timeStamp.epochSeconds,
    )
