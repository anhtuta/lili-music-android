package com.anhtuta.lilimusic.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SortPreferences {
    val preferences: Flow<LibrarySortPreferences>

    suspend fun setSort(
        sort: SongSort,
        order: SortOrder,
    )
}

@Singleton
class SortPreferencesDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : SortPreferences {
        override val preferences: Flow<LibrarySortPreferences> =
            dataStore.data.map { prefs ->
                LibrarySortPreferencesParser.parse(
                    sortBy = prefs[SORT_BY],
                    sortOrder = prefs[SORT_ORDER],
                )
            }

        override suspend fun setSort(
            sort: SongSort,
            order: SortOrder,
        ) {
            dataStore.edit { prefs ->
                prefs[SORT_BY] = sort.apiValue
                prefs[SORT_ORDER] = order.apiValue
            }
        }

        private companion object {
            val SORT_BY = stringPreferencesKey("library_sort_by")
            val SORT_ORDER = stringPreferencesKey("library_sort_order")
        }
    }
