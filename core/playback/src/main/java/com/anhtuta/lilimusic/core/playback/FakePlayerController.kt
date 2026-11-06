package com.anhtuta.lilimusic.core.playback

import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.PlayQueue
import com.anhtuta.lilimusic.core.model.PlaybackQueueState
import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import com.anhtuta.lilimusic.core.model.Song
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePlayerController(
    private val listenCountScheduler: ListenCountScheduler = NoOpListenCountScheduler,
    private val random: Random = Random.Default,
    initialPlayMode: PlayMode = PlayMode.SEQUENCE,
    initialSpeed: Float = PlaybackSpeed.DEFAULT,
    var fakeDurationMs: Long = 180_000L,
) : PlayerController {
    private val _state = MutableStateFlow(PlaybackState(playMode = initialPlayMode, speed = initialSpeed))
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var queueState = PlaybackQueueState(playMode = initialPlayMode)

    override fun play(
        queue: List<Song>,
        startPath: String,
    ) {
        queueState = PlayQueue.start(queue, startPath, queueState.playMode, random)
        publish(playing = true, positionMs = 0L)
    }

    override fun prepare(
        queue: List<Song>,
        startPath: String,
    ) {
        queueState = PlayQueue.start(queue, startPath, queueState.playMode, random)
        publish(playing = false, positionMs = 0L)
    }

    override fun playPause() {
        if (queueState.currentSong == null) return
        publish(playing = !_state.value.isPlaying, positionMs = _state.value.positionMs)
    }

    override fun skipToNext() {
        val next = PlayQueue.skipNext(queueState)
        if (next == null) {
            publish(playing = false, positionMs = _state.value.positionMs, reachedEnd = true)
            return
        }
        queueState = next
        publish(playing = _state.value.isPlaying, positionMs = 0L)
    }

    override fun skipToPrevious() {
        when (
            SkipPreviousPolicy.decide(
                repeatOne = queueState.playMode == PlayMode.REPEAT_ONE,
                currentIndex = queueState.currentIndex,
            )
        ) {
            SkipPreviousAction.ReplayCurrent -> {
                publish(playing = true, positionMs = 0L)
            }
            SkipPreviousAction.StayAtStart -> {
                publish(
                    playing = _state.value.isPlaying,
                    positionMs = _state.value.positionMs,
                    atStartOfQueue = true,
                )
            }
            SkipPreviousAction.GoToPrevious -> {
                queueState = PlayQueue.skipPrevious(queueState)
                publish(playing = _state.value.isPlaying, positionMs = 0L)
            }
        }
    }

    override fun seekTo(positionMs: Long) {
        publish(playing = _state.value.isPlaying, positionMs = positionMs.coerceAtLeast(0L))
    }

    override fun seekBack() {
        seekTo(_state.value.positionMs - PlaybackSeek.INCREMENT_MS)
    }

    override fun seekForward() {
        val next = _state.value.positionMs + PlaybackSeek.INCREMENT_MS
        val duration = _state.value.durationMs
        seekTo(if (duration > 0L) next.coerceAtMost(duration) else next)
    }

    override fun setPlayMode(playMode: PlayMode) {
        queueState = PlayQueue.changeMode(queueState, playMode, random)
        publish(playing = _state.value.isPlaying, positionMs = _state.value.positionMs)
    }

    override fun setSpeed(speed: Float) {
        _state.value = _state.value.copy(speed = PlaybackSpeed.coerce(speed))
    }

    fun advancePosition(positionMs: Long) {
        publish(playing = _state.value.isPlaying, positionMs = positionMs)
    }

    private fun publish(
        playing: Boolean,
        positionMs: Long,
        reachedEnd: Boolean = false,
        atStartOfQueue: Boolean = false,
    ) {
        val previous = _state.value
        val next =
            PlaybackState(
                currentSong = queueState.currentSong,
                queue = queueState.playableQueue,
                isPlaying = playing && queueState.currentSong != null && !reachedEnd,
                positionMs = positionMs,
                durationMs = if (queueState.currentSong == null) 0L else fakeDurationMs,
                playMode = queueState.playMode,
                speed = previous.speed,
                reachedEnd = reachedEnd,
                atStartOfQueue = atStartOfQueue,
            )
        _state.value = next
        listenCountScheduler.apply(
            ListenCountPolicy.decide(
                previousPath = previous.currentSong?.path,
                previousPlaying = previous.isPlaying,
                currentPath = next.currentSong?.path,
                currentPlaying = next.isPlaying,
                positionMs = next.positionMs,
            ),
        )
    }
}

object NoOpListenCountScheduler : ListenCountScheduler {
    override fun apply(decision: ListenCountDecision) = Unit
}

class RecordingListenCountScheduler : ListenCountScheduler {
    val decisions = mutableListOf<ListenCountDecision>()

    override fun apply(decision: ListenCountDecision) {
        decisions += decision
    }
}
