package com.anhtuta.lilimusic.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.PlayMode
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
class PlaybackPreferencesDataSourceTest {
    @Test
    fun persistsAndRestoresPlayModeLastSongAndSpeed() =
        runTest(UnconfinedTestDispatcher()) {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            val dataStore =
                PreferenceDataStoreFactory.create(
                    scope = CoroutineScope(coroutineContext + SupervisorJob()),
                    produceFile = {
                        File(context.cacheDir, "playback-test-${System.nanoTime()}.preferences_pb")
                    },
                )
            val source = PlaybackPreferencesDataSource(dataStore)

            source.preferences.test {
                assertThat(awaitItem()).isEqualTo(PlaybackPreferenceState())

                source.setPlayMode(PlayMode.SHUFFLE)
                assertThat(awaitItem().playMode).isEqualTo(PlayMode.SHUFFLE)

                source.setLastSongPath("hello-artist")
                assertThat(awaitItem().lastSongPath).isEqualTo("hello-artist")

                source.setSpeed(1.3f)
                assertThat(awaitItem().speed).isEqualTo(1.3f)

                source.setLastSongPath(null)
                assertThat(awaitItem().lastSongPath).isNull()
                cancelAndIgnoreRemainingEvents()
            }
        }
}
