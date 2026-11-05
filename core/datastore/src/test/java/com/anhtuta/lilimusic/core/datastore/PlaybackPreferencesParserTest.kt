package com.anhtuta.lilimusic.core.datastore

import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlaybackPreferencesParserTest {
    @Test
    fun missingValuesDefaultToSequenceAndNormalSpeed() {
        assertThat(PlaybackPreferencesParser.parse(null, null, null))
            .isEqualTo(PlaybackPreferenceState())
    }

    @Test
    fun unknownPlayModeFallsBackToSequence() {
        val parsed = PlaybackPreferencesParser.parse("random", "hello", 1.4f)
        assertThat(parsed.playMode).isEqualTo(PlayMode.SEQUENCE)
        assertThat(parsed.lastSongPath).isEqualTo("hello")
        assertThat(parsed.speed).isEqualTo(1.4f)
    }

    @Test
    fun blankPathIsTreatedAsMissing() {
        val parsed = PlaybackPreferencesParser.parse("SHUFFLE", "  ", 2.8f)
        assertThat(parsed.playMode).isEqualTo(PlayMode.SHUFFLE)
        assertThat(parsed.lastSongPath).isNull()
        assertThat(parsed.speed).isEqualTo(PlaybackSpeed.MAX)
    }
}
