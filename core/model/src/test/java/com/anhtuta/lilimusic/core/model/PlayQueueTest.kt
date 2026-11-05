package com.anhtuta.lilimusic.core.model

import com.google.common.truth.Truth.assertThat
import kotlin.random.Random
import org.junit.Test

class PlayQueueTest {
    private val alpha = testSong(id = 1, title = "Alpha", path = "alpha")
    private val beta = testSong(id = 2, title = "Beta", path = "beta")
    private val gamma = testSong(id = 3, title = "Gamma", path = "gamma")
    private val songs = listOf(alpha, beta, gamma)

    @Test
    fun shuffleMovesCurrentSongToFrontAndKeepsEveryTrack() {
        val shuffled = PlayQueue.shuffleCurrentFirst(songs, currentPath = "beta", random = Random(1))

        assertThat(shuffled.first()).isEqualTo(beta)
        assertThat(shuffled).containsExactlyElementsIn(songs)
        assertThat(shuffled).isNotEqualTo(songs)
    }

    @Test
    fun leavingShuffleRestoresOriginalOrderAndRelocatesCurrent() {
        val started = PlayQueue.start(songs, startPath = "gamma", playMode = PlayMode.SHUFFLE, random = Random(2))
        assertThat(started.playableQueue.first().path).isEqualTo("gamma")

        val restored = PlayQueue.changeMode(started, PlayMode.SEQUENCE)

        assertThat(restored.playableQueue).isEqualTo(songs)
        assertThat(restored.currentSong).isEqualTo(gamma)
        assertThat(restored.currentIndex).isEqualTo(2)
    }

    @Test
    fun sequenceStopsAtEndAndDoesNotWrap() {
        val last = PlaybackQueueState(songs, songs, currentIndex = 2, playMode = PlayMode.SEQUENCE)

        assertThat(PlayQueue.skipNext(last)).isNull()
        assertThat(PlayQueue.nextIndex(2, 3, PlayMode.SEQUENCE)).isNull()
        assertThat(PlayQueue.nextIndex(0, 3, PlayMode.SEQUENCE)).isEqualTo(1)
    }

    @Test
    fun shuffleAlsoStopsAtEndOfShuffledList() {
        val last = PlaybackQueueState(songs, songs, currentIndex = 2, playMode = PlayMode.SHUFFLE)

        assertThat(PlayQueue.skipNext(last)).isNull()
    }

    @Test
    fun repeatOneNextAndPreviousStayOnTheSameSong() {
        val state = PlaybackQueueState(songs, songs, currentIndex = 1, playMode = PlayMode.REPEAT_ONE)

        assertThat(PlayQueue.skipNext(state)?.currentSong).isEqualTo(beta)
        assertThat(PlayQueue.skipPrevious(state).currentSong).isEqualTo(beta)
        assertThat(PlayQueue.nextIndex(1, 3, PlayMode.REPEAT_ONE)).isEqualTo(1)
        assertThat(PlayQueue.previousIndex(1, 3, PlayMode.REPEAT_ONE)).isEqualTo(1)
    }

    @Test
    fun previousAtStartStaysOnFirstSong() {
        val first = PlaybackQueueState(songs, songs, currentIndex = 0, playMode = PlayMode.SEQUENCE)

        assertThat(PlayQueue.skipPrevious(first).currentIndex).isEqualTo(0)
    }

    @Test
    fun startInShufflePutsRequestedSongFirst() {
        val state = PlayQueue.start(songs, startPath = "beta", playMode = PlayMode.SHUFFLE, random = Random(7))

        assertThat(state.currentSong).isEqualTo(beta)
        assertThat(state.playableQueue.first()).isEqualTo(beta)
        assertThat(state.originalQueue).isEqualTo(songs)
    }
}
