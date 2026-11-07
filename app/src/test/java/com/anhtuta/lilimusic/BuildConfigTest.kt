package com.anhtuta.lilimusic

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BuildConfigTest {
    @Test
    fun apiBaseUrlPointsAtUnchangedLumenBackend() {
        assertThat(BuildConfig.API_BASE_URL)
            .isEqualTo("https://blog.nenmongvietnam.com/laravel/public")
    }

    @Test
    fun applicationIdMatchesPlayStorePackage() {
        assertThat(BuildConfig.APPLICATION_ID)
            .isEqualTo("com.anhtuta.lilimusic")
    }

    @Test
    fun versionMatchesCommittedDefaults() {
        assertThat(BuildConfig.VERSION_NAME).isEqualTo("1.0.0")
        assertThat(BuildConfig.VERSION_CODE).isEqualTo(1)
    }
}
