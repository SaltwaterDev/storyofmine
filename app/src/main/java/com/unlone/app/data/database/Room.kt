package com.unlone.app.data.database

import android.content.Context
import androidx.room.*
import com.unlone.app.model.Post
import com.unlone.app.utils.Converters
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("select * from DatabasePost WHERE category = :category")
    fun getPostsByCtg(category: String): Flow<List<DatabasePost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<DatabasePost>)
}


@Database(entities = [DatabasePost::class], version = 1)
abstract class PostsDatabase: RoomDatabase() {
    abstract val postDao: PostDao
}
