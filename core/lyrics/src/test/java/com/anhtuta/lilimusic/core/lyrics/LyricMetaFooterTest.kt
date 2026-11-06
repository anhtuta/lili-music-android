package com.anhtuta.lilimusic.core.lyrics

import com.anhtuta.lilimusic.core.model.LyricMeta
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LyricMetaFooterTest {
    @Test
    fun formatsIsoDateAsVietnameseMonthYear() {
        assertThat(LyricMetaFooter.formatDate("2021-06-28")).isEqualTo("6/2021")
    }

    @Test
    fun invalidDateFallsBackToRawString() {
        assertThat(LyricMetaFooter.formatDate("sometime")).isEqualTo("sometime")
    }

    @Test
    fun offsetOnlyMetaHasNoFooter() {
        assertThat(LyricMetaFooter.content(LyricMeta(offsetMs = -200L)).isEmpty).isTrue()
    }

    @Test
    fun buildsWebStyleFields() {
        val content =
            LyricMetaFooter.content(
                LyricMeta(
                    title = "Simple Song",
                    artist = "Test Artist",
                    by = "Tuzaku",
                    source = "manual",
                    date = "2021-06-28",
                ),
            )
        assertThat(content.titleLine).isEqualTo("Simple Song - Test Artist")
        assertThat(content.by).isEqualTo("Tuzaku")
        assertThat(content.source).isEqualTo("manual")
        assertThat(content.date).isEqualTo("6/2021")
    }
}
