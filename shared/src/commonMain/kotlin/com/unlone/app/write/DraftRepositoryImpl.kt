package com.unlone.app.write

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.internal.RealmInstantImpl
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class DraftRepositoryImpl : DraftRepository {

    private val config = RealmConfiguration.Builder(schema = setOf(DraftRealmObject::class))
        .build()
    private val realm: Realm by lazy { Realm.open(config) }


    override fun getAllDraftTitles(): Flow<List<String>> {
        // fetch objects from a realm as Flowables
        val flow: Flow<ResultsChange<DraftRealmObject>> = realm.query<DraftRealmObject>().asFlow()
        return flow.map {
            it.list.toList().map { it1 -> it1.title }
        }
    }

    override fun getCurrentDraft(): Flow<ParentDraftRealmObject?> {
        return realm.query<ParentDraftRealmObject>().asFlow().map { parentDraftResult ->
            val parentDraftList = parentDraftResult.list.toList()
            if (parentDraftList.isNotEmpty()) {
                parentDraftList.maxByOrNull { it.latestDraft().timeStamp }
            } else {
                // todo: create a parent draft
                ParentDraftRealmObject()
            }
        }
    }

    override suspend fun saveDraft(title: String, content: String) {
        // create Draft object
        println(Clock.System.now().epochSeconds)

        realm.writeBlocking {
            copyToRealm(
                DraftRealmObject().apply {
                    this.title = title
                    this.content = content
                }
            )
        }
    }
}