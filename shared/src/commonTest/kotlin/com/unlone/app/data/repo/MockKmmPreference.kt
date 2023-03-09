package com.unlone.app.data.repo

import com.unlone.app.utils.KMMPreference

class MockKmmPreference : KMMPreference {
    override fun put(key: String, value: Int) {

    }

    override fun put(key: String, value: String) {

    }

    override fun put(key: String, value: Boolean) {

    }

    override fun getInt(key: String, default: Int): Int {
        return 1
    }

    override fun getString(key: String): String? {
        return ""
    }

    override fun getBool(key: String, default: Boolean): Boolean {
        return true
    }

    override fun remove(key: String) {}

}