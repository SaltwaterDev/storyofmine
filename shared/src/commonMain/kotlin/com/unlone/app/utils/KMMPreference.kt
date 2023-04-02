package com.unlone.app.utils

interface KMMPreference{
    fun put(key: String, value: Int)
    fun put(key: String, value: String)
    fun put(key: String, value: Boolean)
    fun getInt(key: String, default: Int): Int
    fun getString(key: String): String?
    fun getBool(key: String, default: Boolean): Boolean
    fun remove(key: String)
}
class KMMPreferenceImpl(private val context: KMMContext): KMMPreference {
    override fun put(key: String, value: Int) {
        context.putInt(key, value)
    }

    override fun put(key: String, value: String) {
        context.putString(key, value)
    }

    override fun put(key: String, value: Boolean) {
        context.putBool(key, value)
    }

    override fun getInt(key: String, default: Int): Int = context.getInt(key, default)


    override fun getString(key: String): String? = context.getString(key)


    override fun getBool(key: String, default: Boolean): Boolean =
        context.getBool(key, default)

    override fun remove(key: String) {
        context.remove(key)
    }

}