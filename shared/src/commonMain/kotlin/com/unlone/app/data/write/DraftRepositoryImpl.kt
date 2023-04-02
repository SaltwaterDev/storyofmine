package com.unlone.app.data.write

import co.touchlab.kermit.Logger
import com.unlone.app.domain.entities.Draft
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
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
        return realm.query<ParentDraftRealmObject>().asFlow().map {
            it.list.toList().map { it1 -> it1.toParentDraft() }
        }
    }

    override fun queryDraft(id: String): Flow<Draft> {
        val objectId = ObjectId.from(id)
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

    private fun queryParentDraftById(id: String): ParentDraftRealmObject? {
        return realm.query<ParentDraftRealmObject>("id == $0", ObjectId.from(id)).first().find()
    }


    private val ParentDraftRealmObject.hasDifferentContent: (String, String) -> Boolean
        get() = { title: String, body: String ->
            childDraftRealmObjects.all { it.title != title || it.content != body }
        }

    private fun createChildDraftRealmObject(
        title: String,
        body: String
    ): ChildDraftRealmObject {
        return ChildDraftRealmObject().apply {
            this.title = title
            this.content = body
        }
    }

    override suspend fun addNewVersionToDraft(
        id: String,
        title: String,
        body: String
    ): Pair<String?, String?> {
        return realm.write {
            val parentDraftRealmObject =
                queryParentDraftById(id)?.also { existingParentDraftRealmObject ->
                    findLatest(existingParentDraftRealmObject)?.apply {
                        this.childDraftRealmObjects.add(
                            createChildDraftRealmObject(title, body)
                        )
                    }
                }
            val draftId = parentDraftRealmObject?.id?.toString()
            val latestVersionId =
                parentDraftRealmObject?.childDraftRealmObjects?.last()?.id?.toString()
            Pair(draftId, latestVersionId)
        }
    }

    override suspend fun createNewDraft(title: String, body: String): Pair<String, String> {
        val parentDraftRealmObject = realm.writeBlocking {
            copyToRealm(
                ParentDraftRealmObject().apply {
                    this.childDraftRealmObjects =
                        realmListOf(createChildDraftRealmObject(title, body))
                }
            )
        }

        val draftId = parentDraftRealmObject.id.toString()
        val latestVersionId = parentDraftRealmObject.childDraftRealmObjects.last().id.toString()
        return draftId to latestVersionId
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
        draftId: String,
        title: String,
        body: String
    ): Pair<String, String> {
        return realm.writeBlocking {
            var latestVersionId: ObjectId? = null
            val parentDraft =
                queryParentDraftById(draftId)?.also { existingParentDraftRealmObject ->
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