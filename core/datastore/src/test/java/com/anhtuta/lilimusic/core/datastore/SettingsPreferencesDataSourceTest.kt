package com.anhtuta.lilimusic.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
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
class SettingsPreferencesDataSourceTest {
    @Test
    fun persistsAndRestoresLyricAndAppSettings() =
        runTest(UnconfinedTestDispatcher()) {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            val dataStore =
                PreferenceDataStoreFactory.create(
                    scope = CoroutineScope(coroutineContext + SupervisorJob()),
                    produceFile = {
                        File(context.cacheDir, "settings-test-${System.nanoTime()}.preferences_pb")
                    },
                )
            val source = SettingsPreferencesDataSource(dataStore)

            source.preferences.test {
                assertThat(awaitItem()).isEqualTo(PlayerSettings())

                source.setLyricAlign(LyricAlign.LEFT)
                assertThat(awaitItem().lyricAlign).isEqualTo(LyricAlign.LEFT)

                source.setLyricTheme(LyricTheme.DARK)
                assertThat(awaitItem().lyricTheme).isEqualTo(LyricTheme.DARK)

                source.setKaraokeMode(false)
                assertThat(awaitItem().karaokeMode).isFalse()

                source.setBlurMode(true)
                assertThat(awaitItem().blurMode).isTrue()

                source.setGiganticLine(false)
                assertThat(awaitItem().giganticLine).isFalse()

                source.setWordAppear(true)
                assertThat(awaitItem().wordAppear).isTrue()

                source.setLineAppear(true)
                assertThat(awaitItem().lineAppear).isTrue()

                source.setTranslate(true)
                assertThat(awaitItem().translate).isTrue()

                source.setAppTheme(AppTheme.DARK)
                assertThat(awaitItem().appTheme).isEqualTo(AppTheme.DARK)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
