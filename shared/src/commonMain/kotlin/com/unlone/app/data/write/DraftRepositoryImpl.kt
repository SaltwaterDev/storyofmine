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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

internal class DraftRepositoryImpl : DraftRepository {

    // use the RealmConfiguration.Builder() for more options
    private val configuration = RealmConfiguration.create(
        schema = setOf(
            ParentDraftRealmObject::class,
            ChildDraftRealmObject::class
        )
    )
    private val realm = Realm.open(configuration)


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
        Logger.d { "I am called" }
        return realm.query<ParentDraftRealmObject>("id == $0", objectId)
            .asFlow()
            .map { it.list.firstOrNull()?.toParentDraft() }
            .filterNotNull()
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

    private fun queryParentDraftById(id: ObjectId): ParentDraftRealmObject? {
        return realm.query<ParentDraftRealmObject>("id == $0", id).first()
            .find()
    }


    private val ParentDraftRealmObject.isDifferentContent: (String, String) -> Boolean
        get() = { title: String, body: String ->
            childDraftRealmObjects.all { it.title != title || it.content != body }
        }

    override suspend fun saveDraft(
        id: String?,
        title: String,
        body: String
    ): Pair<String, String> {
        val parentDraftRealmObject = ParentDraftRealmObject().apply {
            id?.let { this.id = ObjectId.from(id) }
            this.childDraftRealmObjects = realmListOf(
                ChildDraftRealmObject().apply {
                    this.title = title
                    this.content = body
                }
            )
        }

        queryParentDraftById(parentDraftRealmObject.id).also { existingParentDraftRealmObject ->
            realm.writeBlocking {
                if (existingParentDraftRealmObject != null) {
                    if (existingParentDraftRealmObject.isDifferentContent(title, body)) {
                        findLatest(existingParentDraftRealmObject)?.apply {
                            childDraftRealmObjects.add(
                                ChildDraftRealmObject().apply {
                                    this.title = title
                                    this.content = body
                                }
                            )
                        }
                    }
                    existingParentDraftRealmObject.topics = parentDraftRealmObject.topics
                } else {
                    copyToRealm(parentDraftRealmObject)
                }
            }
        }
        return parentDraftRealmObject.id.toString() to parentDraftRealmObject.childDraftRealmObjects.last().id.toString()
    }

    override suspend fun updateLastOpenedTime(id: String) {
        realm.write {
            val parentDraftRealmObject =
                query<ParentDraftRealmObject>("id == $0", ObjectId.from(id)).first()
                    .find()
            parentDraftRealmObject?.lastOpened =
                RealmInstant.from(Clock.System.now().epochSeconds, 1000)
        }
    }

    override suspend fun deleteDraft(id: String) = realm.write {
        val parentDraftRealmObject =
            this.query<ParentDraftRealmObject>("id == $0", ObjectId.from(id)).find().first()
        delete(parentDraftRealmObject)
    }

    override suspend fun updateDraftVersion(
        parentDraftId: String,
        title: String,
        body: String
    ): Pair<String, String> {
        return realm.write {
            var latestVersionId: ObjectId? = null
            val parentDraft = queryParentDraftById(ObjectId.from(parentDraftId))?.also { existingParentDraftRealmObject ->
                findLatest(existingParentDraftRealmObject)?.latestDraft()?.apply {
                    latestVersionId = this.id
                    this.title = title
                    this.content = body
                }
            }
            Pair(parentDraft?.id.toString(), latestVersionId.toString())
        }
    }
}