package com.unlone.app.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("select * from DatabasePost WHERE category = :category")
    fun getPostsByCtg(category: String): Flow<List<DatabasePost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<DatabasePost>)

    @Query("DELETE FROM DatabasePost")
    fun nukeTable()
}


@Database(entities = [DatabasePost::class], version = 1)
abstract class PostsDatabase: RoomDatabase() {
    abstract val postDao: PostDao
}
