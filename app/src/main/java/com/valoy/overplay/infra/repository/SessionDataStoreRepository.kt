package com.valoy.overplay.infra.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.valoy.overplay.domain.repository.SessionRepository
import com.valoy.overplay.infra.repository.SessionDataStoreRepository.PreferencesKeys.SESSION_COUNT
import com.valoy.overplay.infra.repository.SessionDataStoreRepository.PreferencesKeys.SESSION_TIME
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionDataStoreRepository @Inject constructor(private val context: Context):
    SessionRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

    override suspend fun saveTime(session: Long) {
        context.dataStore.edit { settings ->
            settings[SESSION_TIME] = session
        }
    }

    override suspend fun getTime(): Long {
        val time = context.dataStore.data.map { preferences ->
            preferences[SESSION_TIME]
        }
        return time.firstOrNull() ?: 0L
    }

    override suspend fun saveCount(session: Int) {
        context.dataStore.edit { settings ->
            settings[SESSION_COUNT] = session
        }
    }

    override suspend fun getCount(): Int {
        val count = context.dataStore.data.map { preferences ->
            preferences[SESSION_COUNT]
        }
        return count.firstOrNull() ?: 0
    }

    private object PreferencesKeys {
        val SESSION_COUNT = intPreferencesKey("session_count")
        val SESSION_TIME = longPreferencesKey("session_time")
    }
}
