package com.nenmongvietnam.lilimusic

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
            .isEqualTo("com.nenmongvietnam.lilimusic")
    }
}
