package com.anhtuta.lilimusic.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings
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
class LyricCardPreferencesDataSourceTest {
    @Test
    fun persistsAndRestoresWebLyricCardKeys() =
        runTest(UnconfinedTestDispatcher()) {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            val dataStore =
                PreferenceDataStoreFactory.create(
                    scope = CoroutineScope(coroutineContext + SupervisorJob()),
                    produceFile = {
                        File(context.cacheDir, "lyric-card-test-${System.nanoTime()}.preferences_pb")
                    },
                )
            val source = LyricCardPreferencesDataSource(dataStore)

            source.preferences.test {
                assertThat(awaitItem()).isEqualTo(LyricCardSettings())

                source.setTextAlign(LyricAlign.CENTER)
                assertThat(awaitItem().textAlign).isEqualTo(LyricAlign.CENTER)

                source.setTextColor("#000000")
                assertThat(awaitItem().textColor).isEqualTo("#000000")

                source.setBold(true)
                assertThat(awaitItem().isBold).isTrue()

                source.setBackgroundColor("#1a1a2e")
                assertThat(awaitItem().backgroundColor).isEqualTo("#1a1a2e")

                source.setUseSongImage(false)
                assertThat(awaitItem().useSongImage).isFalse()

                source.setOverlayOpacity(40)
                assertThat(awaitItem().overlayOpacity).isEqualTo(40)

                source.setBrightnessBoost(20)
                assertThat(awaitItem().brightnessBoost).isEqualTo(20)

                source.setFontFamily(LyricCardFont.GEORGIA)
                assertThat(awaitItem().fontFamily).isEqualTo(LyricCardFont.GEORGIA)

                source.setFontSize(22)
                assertThat(awaitItem().fontSize).isEqualTo(22)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
