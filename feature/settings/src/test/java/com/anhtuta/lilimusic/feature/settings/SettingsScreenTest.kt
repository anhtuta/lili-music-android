package com.anhtuta.lilimusic.feature.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricFontSize
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun chipsAndTogglesFireCallbacks() {
        var align: LyricAlign? = null
        var theme: LyricTheme? = null
        var karaoke: Boolean? = null
        var translate: Boolean? = null
        var appTheme: AppTheme? = null
        var fontSize: LyricFontSize? = null
        var speedUp = 0
        var back = 0
        composeRule.setContent {
            LiliTheme {
                SettingsScreen(
                    state = SettingsUiState(settings = PlayerSettings(), speed = 1.0f),
                    callbacks =
                        SettingsCallbacks(
                            onBack = { back++ },
                            onLyricAlign = { align = it },
                            onLyricTheme = { theme = it },
                            onLyricFontSize = { fontSize = it },
                            onKaraokeMode = { karaoke = it },
                            onBlurMode = {},
                            onWordAppear = {},
                            onLineAppear = {},
                            onGiganticLine = {},
                            onTranslate = { translate = it },
                            onAppTheme = { appTheme = it },
                            onIncreaseSpeed = { speedUp++ },
                            onDecreaseSpeed = {},
                        ),
                )
            }
        }

        composeRule.onNodeWithTag(SettingsTestTags.SCREEN).assertIsDisplayed()
        composeRule.onNodeWithTag(SettingsTestTags.fontSize(LyricFontSize.LARGE)).performClick()
        assertThat(fontSize).isEqualTo(LyricFontSize.LARGE)
        composeRule.onNodeWithTag(SettingsTestTags.ALIGN_LEFT).performScrollTo().performClick()
        assertThat(align).isEqualTo(LyricAlign.LEFT)
        composeRule.onNodeWithTag(SettingsTestTags.THEME_DARK).performScrollTo().performClick()
        assertThat(theme).isEqualTo(LyricTheme.DARK)
        composeRule.onNodeWithTag(SettingsTestTags.KARAOKE).performScrollTo().performClick()
        assertThat(karaoke).isFalse()
        composeRule.onNodeWithTag(SettingsTestTags.TRANSLATE).performScrollTo().performClick()
        assertThat(translate).isTrue()
        composeRule.onNodeWithTag(SettingsTestTags.APP_THEME_DARK).performScrollTo().performClick()
        assertThat(appTheme).isEqualTo(AppTheme.DARK)
        composeRule.onNodeWithTag(SettingsTestTags.SPEED_UP).performScrollTo().performClick()
        assertThat(speedUp).isEqualTo(1)
        composeRule.onNodeWithTag(SettingsTestTags.BACK).performClick()
        assertThat(back).isEqualTo(1)
    }

    @Test
    fun darkThemeBackIconStaysFindable() {
        composeRule.setContent {
            LiliTheme(darkTheme = true) {
                SettingsScreen(
                    state = SettingsUiState(settings = PlayerSettings(), speed = 1.0f),
                    callbacks =
                        SettingsCallbacks(
                            onBack = {},
                            onLyricAlign = {},
                            onLyricTheme = {},
                            onLyricFontSize = {},
                            onKaraokeMode = {},
                            onBlurMode = {},
                            onWordAppear = {},
                            onLineAppear = {},
                            onGiganticLine = {},
                            onTranslate = {},
                            onAppTheme = {},
                            onIncreaseSpeed = {},
                            onDecreaseSpeed = {},
                        ),
                )
            }
        }
        composeRule.onNodeWithTag(SettingsTestTags.BACK).assertIsDisplayed()
    }
}
