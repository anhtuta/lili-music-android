package com.anhtuta.lilimusic.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.SongSort
import com.anhtuta.lilimusic.core.model.SortOrder
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SortPreferencesDataSourceTest {
    @Test
    fun persistsAndRestoresSort() =
        runTest(UnconfinedTestDispatcher()) {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            val dataStore =
                PreferenceDataStoreFactory.create(
                    scope = CoroutineScope(coroutineContext + SupervisorJob()),
                    produceFile = {
                        File(context.cacheDir, "sort-test-${System.nanoTime()}.preferences_pb")
                    },
                )
            val source = SortPreferencesDataSource(dataStore)

            source.preferences.test {
                assertThat(awaitItem()).isEqualTo(LibrarySortPreferences())

                source.setSort(SongSort.LISTENS, SortOrder.DESC)

                assertThat(awaitItem()).isEqualTo(
                    LibrarySortPreferences(SongSort.LISTENS, SortOrder.DESC),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
}
