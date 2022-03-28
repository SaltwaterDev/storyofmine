package com.unlone.app.di

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.unlone.app.data.database.PostDao
import com.unlone.app.data.database.PostsDatabase
import com.unlone.app.utils.Converters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideConverters(moshi: Moshi): Converters {
        return Converters(moshi)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
        converters: Converters
    ): PostsDatabase {
        return Room.databaseBuilder(
            appContext,
            PostsDatabase::class.java,
            "PostsDatabase"
        ).addTypeConverter(converters).build()
    }

    @Provides
    @Singleton
    fun providePostDao(postsDatabase: PostsDatabase): PostDao {
        return postsDatabase.postDao
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Singleton // Provide always the same instance
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        // Run this code when providing an instance of CoroutineScope
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile("settings") }
        )
    }

}

