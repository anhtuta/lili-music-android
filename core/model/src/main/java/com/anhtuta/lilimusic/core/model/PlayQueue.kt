package com.anhtuta.lilimusic.core.model

import kotlin.random.Random

data class PlaybackQueueState(
    val originalQueue: List<Song> = emptyList(),
    val playableQueue: List<Song> = emptyList(),
    val currentIndex: Int = 0,
    val playMode: PlayMode = PlayMode.SEQUENCE,
) {
    val currentSong: Song? get() = playableQueue.getOrNull(currentIndex)
}

object PlayQueue {
    fun start(
        queue: List<Song>,
        startPath: String,
        playMode: PlayMode,
        random: Random = Random.Default,
    ): PlaybackQueueState {
        val original = queue
        val startIndex = indexOfPath(original, startPath).coerceAtLeast(0)
        val base =
            PlaybackQueueState(
                originalQueue = original,
                playableQueue = original,
                currentIndex = startIndex,
                playMode = PlayMode.SEQUENCE,
            )
        return if (playMode == PlayMode.SEQUENCE) {
            base
        } else {
            changeMode(base, playMode, random)
        }
    }

    fun changeMode(
        state: PlaybackQueueState,
        playMode: PlayMode,
        random: Random = Random.Default,
    ): PlaybackQueueState {
        val currentPath = state.currentSong?.path
        val playable =
            when {
                playMode == PlayMode.SHUFFLE && currentPath != null -> {
                    shuffleCurrentFirst(state.originalQueue, currentPath, random)
                }
                else -> state.originalQueue
            }
        val index =
            if (currentPath == null) {
                0
            } else {
                indexOfPath(playable, currentPath).coerceAtLeast(0)
            }
        return state.copy(
            playableQueue = playable,
            currentIndex = index,
            playMode = playMode,
        )
    }

    fun skipNext(state: PlaybackQueueState): PlaybackQueueState? {
        val nextIndex = nextIndex(state.currentIndex, state.playableQueue.size, state.playMode) ?: return null
        return state.copy(currentIndex = nextIndex)
    }

    fun skipPrevious(state: PlaybackQueueState): PlaybackQueueState {
        val previousIndex = previousIndex(state.currentIndex, state.playableQueue.size, state.playMode)
        return state.copy(currentIndex = previousIndex)
    }

    fun shuffleCurrentFirst(
        songs: List<Song>,
        currentPath: String,
        random: Random = Random.Default,
    ): List<Song> {
        if (songs.size <= 1) return songs
        val shuffled = songs.toMutableList()
        for (index in shuffled.lastIndex downTo 1) {
            val swapWith = random.nextInt(index + 1)
            val current = shuffled[index]
            shuffled[index] = shuffled[swapWith]
            shuffled[swapWith] = current
        }
        val currentIndex = indexOfPath(shuffled, currentPath)
        if (currentIndex > 0) {
            val current = shuffled.removeAt(currentIndex)
            shuffled.add(0, current)
        }
        return shuffled
    }

    fun nextIndex(
        currentIndex: Int,
        size: Int,
        playMode: PlayMode,
    ): Int? {
        if (size <= 0 || currentIndex !in 0 until size) return null
        return when (playMode) {
            PlayMode.REPEAT_ONE -> currentIndex
            PlayMode.SEQUENCE, PlayMode.SHUFFLE -> {
                val next = currentIndex + 1
                if (next >= size) null else next
            }
        }
    }

    fun previousIndex(
        currentIndex: Int,
        size: Int,
        playMode: PlayMode,
    ): Int {
        if (size <= 0) return 0
        return when (playMode) {
            PlayMode.REPEAT_ONE -> currentIndex.coerceIn(0, size - 1)
            PlayMode.SEQUENCE, PlayMode.SHUFFLE -> (currentIndex - 1).coerceAtLeast(0)
        }
    }

    fun indexOfPath(
        songs: List<Song>,
        path: String,
    ): Int = songs.indexOfFirst { it.path == path }
}
