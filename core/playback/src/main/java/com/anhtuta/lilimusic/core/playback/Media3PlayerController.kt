@file:OptIn(UnstableApi::class)

package com.anhtuta.lilimusic.core.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.anhtuta.lilimusic.core.datastore.PlaybackPreferences
import com.anhtuta.lilimusic.core.model.PlayMode
import com.anhtuta.lilimusic.core.model.PlayQueue
import com.anhtuta.lilimusic.core.model.PlaybackQueueState
import com.anhtuta.lilimusic.core.model.PlaybackSpeed
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.playback.di.PlaybackScope
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@Singleton
class Media3PlayerController
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val playbackPreferences: PlaybackPreferences,
        private val listenCountScheduler: ListenCountScheduler,
        @PlaybackScope private val scope: CoroutineScope,
    ) : PlayerController {
        private val _state = MutableStateFlow(PlaybackState())
        override val state: StateFlow<PlaybackState> = _state.asStateFlow()

        private var controller: MediaController? = null
        private var queueState = PlaybackQueueState()
        private var positionJob: Job? = null
        private val random = Random.Default

        private val listener =
            object : Player.Listener {
                override fun onEvents(
                    player: Player,
                    events: Player.Events,
                ) {
                    publishFromPlayer(player)
                }

                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int,
                ) {
                    val path = mediaItem?.mediaId ?: return
                    val index = PlayQueue.indexOfPath(queueState.playableQueue, path)
                    if (index >= 0) {
                        queueState = queueState.copy(currentIndex = index)
                    }
                    persistLastSong()
                    controller?.let(::publishFromPlayer)
                }
            }

        init {
            scope.launch {
                val prefs = playbackPreferences.preferences.first()
                queueState = queueState.copy(playMode = prefs.playMode)
                _state.value = _state.value.copy(playMode = prefs.playMode, speed = prefs.speed)
                val mediaController = connectController()
                mediaController.addListener(listener)
                mediaController.setPlaybackSpeed(prefs.speed)
                applyRepeatMode(mediaController, prefs.playMode)
                controller = mediaController
                publishFromPlayer(mediaController)
                startPositionTicker()
            }
        }

        private suspend fun connectController(): MediaController =
            suspendCancellableCoroutine { continuation ->
                val token = SessionToken(context, ComponentName(context, LiliPlaybackService::class.java))
                val future = MediaController.Builder(context, token).buildAsync()
                future.addListener(
                    {
                        runCatching { future.get() }
                            .onSuccess { continuation.resume(it) }
                            .onFailure { continuation.resumeWithException(it) }
                    },
                    ContextCompat.getMainExecutor(context),
                )
                continuation.invokeOnCancellation { MediaController.releaseFuture(future) }
            }

        override fun play(
            queue: List<Song>,
            startPath: String,
        ) {
            scope.launch { applyQueue(queue, startPath, playWhenReady = true) }
        }

        override fun prepare(
            queue: List<Song>,
            startPath: String,
        ) {
            scope.launch { applyQueue(queue, startPath, playWhenReady = false) }
        }

        override fun playPause() {
            withController { mediaController ->
                if (mediaController.isPlaying) {
                    mediaController.pause()
                } else {
                    mediaController.play()
                }
            }
        }

        override fun skipToNext() {
            withController { mediaController ->
                if (queueState.playMode == PlayMode.REPEAT_ONE) {
                    mediaController.seekTo(0)
                    mediaController.play()
                    return@withController
                }
                val next = PlayQueue.skipNext(queueState)
                if (next == null) {
                    mediaController.pause()
                    publishFromPlayer(mediaController, reachedEnd = true)
                } else {
                    queueState = next
                    mediaController.seekTo(next.currentIndex, 0L)
                    persistLastSong()
                }
            }
        }

        override fun skipToPrevious() {
            withController { mediaController ->
                when (
                    SkipPreviousPolicy.decide(
                        repeatOne = queueState.playMode == PlayMode.REPEAT_ONE,
                        currentIndex = queueState.currentIndex,
                    )
                ) {
                    SkipPreviousAction.ReplayCurrent -> {
                        mediaController.seekTo(0)
                        mediaController.play()
                    }
                    SkipPreviousAction.StayAtStart -> {
                        publishFromPlayer(mediaController, atStartOfQueue = true)
                    }
                    SkipPreviousAction.GoToPrevious -> {
                        queueState = PlayQueue.skipPrevious(queueState)
                        mediaController.seekTo(queueState.currentIndex, 0L)
                        persistLastSong()
                    }
                }
            }
        }

        override fun seekTo(positionMs: Long) {
            withController { mediaController ->
                mediaController.seekTo(positionMs.coerceAtLeast(0L))
            }
        }

        override fun seekBack() {
            withController { mediaController -> mediaController.seekBack() }
        }

        override fun seekForward() {
            withController { mediaController -> mediaController.seekForward() }
        }

        override fun setPlayMode(playMode: PlayMode) {
            withController { mediaController ->
                val currentPath = queueState.currentSong?.path
                queueState = PlayQueue.changeMode(queueState, playMode, random)
                applyRepeatMode(mediaController, playMode)
                if (currentPath != null && queueState.playableQueue.isNotEmpty()) {
                    mediaController.setMediaItems(
                        SongMediaItems.from(queueState.playableQueue),
                        queueState.currentIndex,
                        mediaController.currentPosition,
                    )
                    mediaController.prepare()
                }
                scope.launch { playbackPreferences.setPlayMode(playMode) }
                publishFromPlayer(mediaController)
            }
        }

        override fun setSpeed(speed: Float) {
            val coerced = PlaybackSpeed.coerce(speed)
            withController { mediaController ->
                mediaController.setPlaybackSpeed(coerced)
            }
            _state.value = _state.value.copy(speed = coerced)
            scope.launch { playbackPreferences.setSpeed(coerced) }
        }

        private suspend fun applyQueue(
            queue: List<Song>,
            startPath: String,
            playWhenReady: Boolean,
        ) {
            val mediaController = awaitController() ?: return
            queueState = PlayQueue.start(queue, startPath, queueState.playMode, random)
            val startIndex = queueState.currentIndex.coerceAtLeast(0)
            mediaController.setMediaItems(SongMediaItems.from(queueState.playableQueue), startIndex, 0L)
            applyRepeatMode(mediaController, queueState.playMode)
            mediaController.prepare()
            mediaController.playWhenReady = playWhenReady
            persistLastSong()
            publishFromPlayer(mediaController)
        }

        private fun applyRepeatMode(
            mediaController: MediaController,
            playMode: PlayMode,
        ) {
            mediaController.repeatMode =
                if (playMode == PlayMode.REPEAT_ONE) {
                    Player.REPEAT_MODE_ONE
                } else {
                    Player.REPEAT_MODE_OFF
                }
        }

        private fun publishFromPlayer(
            player: Player,
            reachedEnd: Boolean = player.playbackState == Player.STATE_ENDED,
            atStartOfQueue: Boolean = false,
        ) {
            val previous = _state.value
            val duration = player.duration.takeUnless { it == C.TIME_UNSET } ?: 0L
            val next =
                PlaybackState(
                    currentSong = queueState.currentSong,
                    queue = queueState.playableQueue,
                    isPlaying = player.isPlaying,
                    positionMs = player.currentPosition.coerceAtLeast(0L),
                    durationMs = duration.coerceAtLeast(0L),
                    playMode = queueState.playMode,
                    speed = player.playbackParameters.speed,
                    reachedEnd = reachedEnd && !player.isPlaying,
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

        private fun persistLastSong() {
            val path = queueState.currentSong?.path ?: return
            scope.launch { playbackPreferences.setLastSongPath(path) }
        }

        private fun startPositionTicker() {
            positionJob?.cancel()
            positionJob =
                scope.launch {
                    while (true) {
                        delay(POSITION_TICK_MS)
                        val mediaController = controller
                        if (mediaController != null && mediaController.isPlaying) {
                            _state.value =
                                _state.value.copy(
                                    positionMs = mediaController.currentPosition.coerceAtLeast(0L),
                                    durationMs =
                                        mediaController.duration
                                            .takeUnless { it == C.TIME_UNSET }
                                            ?.coerceAtLeast(0L)
                                            ?: _state.value.durationMs,
                                )
                        }
                    }
                }
        }

        private fun withController(block: (MediaController) -> Unit) {
            val mediaController = controller
            if (mediaController != null) {
                block(mediaController)
            } else {
                scope.launch {
                    awaitController()?.let(block)
                }
            }
        }

        private suspend fun awaitController(): MediaController? {
            controller?.let { return it }
            repeat(50) {
                delay(100)
                controller?.let { return it }
            }
            return controller
        }

        companion object {
            const val POSITION_TICK_MS = 50L
        }
    }
