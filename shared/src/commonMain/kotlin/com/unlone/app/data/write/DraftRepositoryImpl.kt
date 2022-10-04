package com.unlone.app.data.write

import co.touchlab.kermit.Logger
import com.unlone.app.domain.entities.Draft
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal class DraftRepositoryImpl : DraftRepository {

    private val config =
        RealmConfiguration.Builder(
            schema = setOf(
                ParentDraftRealmObject::class,
                ChildDraftRealmObject::class
            )
        ).build()
    private val realm: Realm by lazy {
        Realm.open(config)
    }


    override fun getAllDrafts(): Flow<List<Draft>> {
        // fetch objects from a realm as Flowables
        val flow: Flow<ResultsChange<ParentDraftRealmObject>> =
            realm.query<ParentDraftRealmObject>().asFlow()
        return flow.map {
            it.list.toList().map { it1 -> it1.toParentDraft() }
        }
    }

    override fun queryDraft(id: String): Flow<Draft> {
        val objectId = ObjectId.from(id)
        return realm.query<ParentDraftRealmObject>("id == $0", objectId)
            .asFlow()
            .map { it.list.first().toParentDraft() }
    }

    override fun getLastOpenedDraft(): Flow<Draft?> {
        return realm.query<ParentDraftRealmObject>().asFlow().map { parentDraftResult ->
            val parentDraftList = parentDraftResult.list.toList()
            val parentRealmObject = if (parentDraftList.isNotEmpty()) {
                parentDraftList.maxByOrNull { it.lastOpened }
                    ?: throw Exception("Failed to get the latest draft")
            } else null
            Logger.d(parentRealmObject?.toParentDraft().toString())
            parentRealmObject?.toParentDraft()
        }
    }

    override suspend fun saveDraft(id: String?, title: String, content: String) {
        realm.write {
            val parentDraftRealmObject = ParentDraftRealmObject().apply {
                id?.let { this.id = ObjectId.from(id) }
                this.childDraftRealmObjects = realmListOf(
                    ChildDraftRealmObject().apply {
                        this.title = title
                        this.content = content
                    }
                )
            }

            val existingParentDraftRealmObject: ParentDraftRealmObject? =
                query<ParentDraftRealmObject>("id == $0", parentDraftRealmObject.id).first()
                    .find()
            if (existingParentDraftRealmObject != null) {
                if (existingParentDraftRealmObject.childDraftRealmObjects.all { it.title != title || it.content != content }) {
                    // insert new Version
                    parentDraftRealmObject.childDraftRealmObjects.forEach {
                        existingParentDraftRealmObject.childDraftRealmObjects.add(it)
                    }
                    existingParentDraftRealmObject.topics = parentDraftRealmObject.topics
                } else {
                    return@write
                }
            } else {
                copyToRealm(parentDraftRealmObject)
            }
        }
    }

    override suspend fun updateLastOpenedTime(id: String) {
        realm.write {
            val parentDraftRealmObject: ParentDraftRealmObject? =
                this.query<ParentDraftRealmObject>("id == $0", ObjectId.from(id)).first().find()
            // modify the frog's age in the write transaction to persist the new age to the realm
            parentDraftRealmObject?.lastOpened =
                RealmInstant.from(Clock.System.now().epochSeconds, 1000)
        }
    }

    override suspend fun deleteDraft(id: String) = realm.write {
        val parentDraftRealmObject =
            this.query<ParentDraftRealmObject>("id == $0", ObjectId.from(id)).find().first()
        delete(parentDraftRealmObject)
    }
}