package macc.AR.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import macc.AR.domain.manager.LocalUserManager
import macc.AR.util.Constants
import macc.AR.util.Constants.USER_SETTINGS

/*
* Implementation of user manager, it implements method to store user preferences on a datastore,
* accessible from the application context*/
class LocalUserManagerImpl(
    private val context: Context
): LocalUserManager{
    override suspend fun saveAppEntry() {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.APP_ENTRY] = true
        }

    }

    override fun readAppEntry(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.APP_ENTRY] ?: false
        }
    }

}

// Define a datastore to keep user preferences
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_SETTINGS)

// Define a boolean preferences
private object PreferencesKeys{
    val APP_ENTRY = booleanPreferencesKey(name = Constants.APP_ENTRY)
}