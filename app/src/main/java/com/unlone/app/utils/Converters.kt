package com.unlone.app.utils

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.unlone.app.data.database.DatabasePost
import javax.inject.Inject

@ProvidedTypeConverter
class Converters @Inject constructor(private val moshi: Moshi) {
    @TypeConverter
    fun fromJson(value: String): DatabasePost? {
        val jsonAdapter: JsonAdapter<DatabasePost> = moshi.adapter(DatabasePost::class.java)

        return jsonAdapter.fromJson(value)
    }

    @TypeConverter
    fun postToJson(post: DatabasePost): String? {
        val jsonAdapter: JsonAdapter<DatabasePost> = moshi.adapter(DatabasePost::class.java)

        return jsonAdapter.toJson(post)
    }

    @TypeConverter
    fun listToString(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun stringToList(string: String): List<String> {
        return string.split(",").map { it.trim() }
    }


}
