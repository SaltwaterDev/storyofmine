package com.unlone.app.di

import android.content.Context
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
}
