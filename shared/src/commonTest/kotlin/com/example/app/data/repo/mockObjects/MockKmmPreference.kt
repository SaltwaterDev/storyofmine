package com.example.app.data.repo.mockObjects

import com.example.app.utils.KMMPreference

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