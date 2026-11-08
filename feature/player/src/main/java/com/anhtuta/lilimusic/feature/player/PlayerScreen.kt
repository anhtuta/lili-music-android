package com.anhtuta.lilimusic.feature.player

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anhtuta.lilimusic.core.model.Song
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton
import com.anhtuta.lilimusic.core.ui.download.DownloadActionButton
import com.anhtuta.lilimusic.core.ui.palette.CoverPalette
import com.anhtuta.lilimusic.core.ui.palette.CoverPaletteColors
import com.anhtuta.lilimusic.core.ui.text.MarqueeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    modifier: Modifier = Modifier,
    lyrics: @Composable () -> Unit = {},
    coverPaletteOverride: CoverPaletteColors? = null,
) {
    val loadedPalette = rememberCoverPalette(state.song?.imageUrl)
    val palette = coverPaletteOverride ?: loadedPalette
    var lyricsExpanded by remember { mutableStateOf(false) }
    val dismissScroll = rememberNowPlayingDismissScroll(enabled = !lyricsExpanded, onDismiss = callbacks.onBack)
    BackHandler(enabled = lyricsExpanded) { lyricsExpanded = false }
    val isDark = !CoverPalette.isLight(MaterialTheme.colorScheme.background)
    val gradient = CoverPalette.nowPlayingGradient(palette?.background, isDark)
    Scaffold(
        modifier =
            modifier
                .testTag(PlayerTestTags.SCREEN)
                .nestedScroll(dismissScroll),
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0f),
        contentColor = palette?.onBackground ?: MaterialTheme.colorScheme.onSurface,
        topBar = {
            NowPlayingTopBar(
                state = state,
                onCollapseOrBack = {
                    if (lyricsExpanded) {
                        lyricsExpanded = false
                    } else {
                        callbacks.onBack()
                    }
                },
                callbacks = callbacks,
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(gradient)
                    .testTag(PlayerTestTags.BACKGROUND),
        ) {
            if (palette != null) {
                Box(modifier = Modifier.testTag(PlayerTestTags.PALETTE))
            }
            PlayerBody(
                state = state,
                callbacks = callbacks,
                lyrics = lyrics,
                lyricsExpanded = lyricsExpanded,
                onLyricsExpandedChange = { lyricsExpanded = it },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

internal object AppBarActionMetrics {
    val IconSize = 20.dp
    val ContainerSize = 40.dp
}

internal object NowPlayingAppBarStyle {
    const val ARTIST_ALPHA = 0.65f

    fun artistColor(onSurface: Color): Color = onSurface.copy(alpha = ARTIST_ALPHA)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NowPlayingTopBar(
    state: PlayerUiState,
    onCollapseOrBack: () -> Unit,
    callbacks: PlayerCallbacks,
) {
    TopAppBar(
        title = { NowPlayingAppBarTitle(song = state.song) },
        navigationIcon = {
            LiliIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                label = stringResource(R.string.player_back),
                onClick = onCollapseOrBack,
                modifier = Modifier.testTag(PlayerTestTags.BACK),
            )
        },
        actions = {
            if (state.song != null) {
                DownloadActionButton(
                    status = state.downloadStatus,
                    progressPercent = state.downloadProgress,
                    onClick = callbacks.onDownload,
                    modifier = Modifier.testTag(PlayerTestTags.DOWNLOAD),
                    iconSize = AppBarActionMetrics.IconSize,
                    containerSize = AppBarActionMetrics.ContainerSize,
                )
                LiliIconButton(
                    icon = Icons.Filled.Image,
                    label = stringResource(R.string.player_lyric_card),
                    onClick = callbacks.onLyricCard,
                    modifier = Modifier.testTag(PlayerTestTags.LYRIC_CARD),
                    iconSize = AppBarActionMetrics.IconSize,
                    containerSize = AppBarActionMetrics.ContainerSize,
                )
            }
            LiliIconButton(
                icon = Icons.Filled.Settings,
                label = stringResource(R.string.player_settings),
                onClick = callbacks.onSettings,
                modifier = Modifier.testTag(PlayerTestTags.SETTINGS),
                iconSize = AppBarActionMetrics.IconSize,
                containerSize = AppBarActionMetrics.ContainerSize,
            )
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
    )
}

@Composable
private fun NowPlayingAppBarTitle(song: Song?) {
    if (song == null) {
        return
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NowPlayingAppBarCover(song = song)
        Column(modifier = Modifier.weight(1f)) {
            MarqueeText(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag(PlayerTestTags.APP_BAR_TITLE),
            )
            MarqueeText(
                text = song.artist,
                color = NowPlayingAppBarStyle.artistColor(MaterialTheme.colorScheme.onSurface),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.testTag(PlayerTestTags.APP_BAR_ARTIST),
            )
        }
    }
}

@Composable
private fun NowPlayingAppBarCover(song: Song) {
    val shape = RoundedCornerShape(8.dp)
    if (song.imageUrl == null) {
        Box(
            modifier =
                Modifier
                    .size(36.dp)
                    .clip(shape)
                    .testTag(PlayerTestTags.APP_BAR_COVER),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = song.title.take(1), style = MaterialTheme.typography.labelLarge)
        }
    } else {
        AsyncImage(
            model = song.imageUrl,
            contentDescription = stringResource(R.string.player_cover_cd, song.title),
            modifier =
                Modifier
                    .size(36.dp)
                    .clip(shape)
                    .testTag(PlayerTestTags.APP_BAR_COVER),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun PlayerBody(
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    lyrics: @Composable () -> Unit,
    lyricsExpanded: Boolean,
    onLyricsExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val song = state.song
    if (song == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.player_empty))
        }
        return
    }
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (PlayerLayout.isCompact(maxWidth)) {
            CompactPlayerBody(
                song = song,
                state = state,
                callbacks = callbacks,
                lyrics = lyrics,
                lyricsExpanded = lyricsExpanded,
                onLyricsExpandedChange = onLyricsExpandedChange,
            )
        } else {
            ExpandedPlayerBody(
                song = song,
                state = state,
                callbacks = callbacks,
                lyrics = lyrics,
                lyricsExpanded = lyricsExpanded,
                onLyricsExpandedChange = onLyricsExpandedChange,
            )
        }
    }
}

@Composable
private fun CompactPlayerBody(
    song: Song,
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    lyrics: @Composable () -> Unit,
    lyricsExpanded: Boolean,
    onLyricsExpandedChange: (Boolean) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .animateContentSize(),
    ) {
        LyricSlot(
            lyrics = lyrics,
            expanded = lyricsExpanded,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )
        PlayerLyricHandle(
            expanded = lyricsExpanded,
            onExpandedChange = onLyricsExpandedChange,
        )
        if (!lyricsExpanded) {
            PlayerControlsBar(
                song = song,
                state = state,
                callbacks = callbacks,
                showHeadings = false,
            )
        }
    }
}

@Composable
private fun ExpandedPlayerBody(
    song: Song,
    state: PlayerUiState,
    callbacks: PlayerCallbacks,
    lyrics: @Composable () -> Unit,
    lyricsExpanded: Boolean,
    onLyricsExpandedChange: (Boolean) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .animateContentSize(),
    ) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            if (!lyricsExpanded) {
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    PlayerCover(title = song.title, imageUrl = song.imageUrl)
                }
            }
            LyricSlot(
                lyrics = lyrics,
                expanded = lyricsExpanded,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
        PlayerLyricHandle(
            expanded = lyricsExpanded,
            onExpandedChange = onLyricsExpandedChange,
        )
        if (!lyricsExpanded) {
            PlayerControlsBar(
                song = song,
                state = state,
                callbacks = callbacks,
                showHeadings = false,
            )
        }
    }
}

@Composable
private fun LyricSlot(
    lyrics: @Composable () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(LyricPaneShape.CornerRadius))
                .testTag(PlayerTestTags.LYRICS),
    ) {
        if (expanded) {
            Box(modifier = Modifier.testTag(PlayerTestTags.LYRICS_EXPANDED))
        }
        lyrics()
    }
}

@Composable
private fun PlayerCover(
    title: String,
    imageUrl: String?,
) {
    if (imageUrl == null) {
        val placeholderDescription = stringResource(R.string.player_cover_placeholder_cd, title)
        Box(
            modifier =
                Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag(PlayerTestTags.COVER_PLACEHOLDER)
                    .semantics { contentDescription = placeholderDescription },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = title.take(1), style = MaterialTheme.typography.displayLarge)
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.player_cover_cd, title),
            modifier =
                Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag(PlayerTestTags.COVER),
            contentScale = ContentScale.Crop,
        )
    }
}
