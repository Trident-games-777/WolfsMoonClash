package com.lilithgame.hgame.g.repository

import android.content.SharedPreferences
import androidx.core.content.edit

class WolfMoonRepository(
    private val sharedPreferences: SharedPreferences
) {
    fun getIsSaved(): Boolean = sharedPreferences.getBoolean(SAVED_KEY, false)

    fun getUrl(): String = sharedPreferences.getString(URL_KEY, "")!!

    fun save(url: String) {
        sharedPreferences.edit {
            putBoolean(SAVED_KEY, true)
            putString(URL_KEY, url)
            apply()
        }
    }

    private companion object {
        private const val SAVED_KEY = "is_saved"
        private const val URL_KEY = "url"
    }
}