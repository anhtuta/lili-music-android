package com.anhtuta.lilimusic.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LiliThemeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun lightThemeProvidesMaterialColorScheme() {
        var primary: Color? = null
        composeRule.setContent {
            LiliTheme(darkTheme = false) {
                primary = MaterialTheme.colorScheme.primary
            }
        }
        composeRule.waitForIdle()
        assertThat(primary).isNotNull()
        assertThat(primary).isNotEqualTo(Color.Unspecified)
    }

    @Test
    fun darkThemeUsesDifferentSchemeFromLight() {
        var lightBackground: Color? = null
        var darkBackground: Color? = null
        composeRule.setContent {
            LiliTheme(darkTheme = false) {
                lightBackground = MaterialTheme.colorScheme.background
            }
        }
        composeRule.setContent {
            LiliTheme(darkTheme = true) {
                darkBackground = MaterialTheme.colorScheme.background
            }
        }
        composeRule.waitForIdle()
        assertThat(lightBackground).isNotEqualTo(darkBackground)
    }
}
