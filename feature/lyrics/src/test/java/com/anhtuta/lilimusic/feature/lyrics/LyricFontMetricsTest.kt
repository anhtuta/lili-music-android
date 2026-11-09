package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.sp
import com.anhtuta.lilimusic.core.model.LyricFontSize
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LyricFontMetricsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun largePresetScalesBodySize() {
        lateinit var typography: Typography
        var scaled = 0.sp
        composeRule.setContent {
            LiliTheme {
                typography = MaterialTheme.typography
                scaled =
                    LyricFontMetrics.bodyFontSize(
                        typography = typography,
                        settings = PlayerSettings(lyricFontSize = LyricFontSize.LARGE),
                        giganticActive = false,
                    )
            }
        }
        assertThat(scaled.value).isWithin(0.01f).of(typography.bodyLarge.fontSize.value * 1.3f)
    }
}
