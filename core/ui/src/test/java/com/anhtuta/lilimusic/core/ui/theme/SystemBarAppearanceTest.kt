package com.anhtuta.lilimusic.core.ui.theme

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SystemBarAppearanceTest {
    @Test
    fun lightThemeUsesDarkStatusBarIcons() {
        assertThat(SystemBarAppearance.isAppearanceLightStatusBars(darkTheme = false)).isTrue()
        assertThat(SystemBarAppearance.isAppearanceLightStatusBars(darkTheme = true)).isFalse()
    }
}
