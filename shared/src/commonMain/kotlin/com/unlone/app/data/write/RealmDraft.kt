package com.unlone.app.data.write

import com.unlone.app.domain.entities.Draft
import com.unlone.app.domain.entities.DraftVersion
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


// classes for realm
internal class ParentDraftRealmObject : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId.create()
    var childDraftRealmObjects: RealmList<ChildDraftRealmObject> = realmListOf()
    var topics: List<String> = emptyList()
    var lastOpened: RealmInstant = RealmInstant.from(Clock.System.now().epochSeconds, 1000)
}



internal fun ParentDraftRealmObject.latestDraft(): ChildDraftRealmObject {
    return childDraftRealmObjects.maxByOrNull { it.timeStamp } ?: childDraftRealmObjects.first()
}

internal fun ParentDraftRealmObject.toParentDraft() =
    Draft(
        id = this.id.toString(),
        draftVersions = this.childDraftRealmObjects
            .toList()
            .map { it1 -> it1.toChildDraft() },
        lastOpened = Instant.fromEpochMilliseconds(this.lastOpened.epochSeconds)
    )


internal class ChildDraftRealmObject : RealmObject {
    var id: ObjectId = ObjectId.create()
    var title: String = ""
    var content: String = ""
    var timeStamp: RealmInstant = RealmInstant.from(Clock.System.now().epochSeconds, 1000)
}

internal fun ChildDraftRealmObject.toChildDraft() =
    DraftVersion(
        this.id.toString(),
        this.title,
        this.content,
        this.timeStamp.epochSeconds,
    )
