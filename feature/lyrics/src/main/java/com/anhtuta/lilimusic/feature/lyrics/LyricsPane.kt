package com.anhtuta.lilimusic.feature.lyrics

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anhtuta.lilimusic.core.lyrics.LyricLineFrame
import com.anhtuta.lilimusic.core.lyrics.LyricMetaFooter
import com.anhtuta.lilimusic.core.lyrics.LyricSyncFrame
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton

@Composable
fun LyricsPane(
    state: LyricsUiState,
    onNudgeOffset: (Long) -> Unit,
    onSeekToLine: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onLyricCardClick: () -> Unit = {},
) {
    val style = state.settings.toVisualStyle()
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .testTag(LyricsTestTags.PANE)
                .background(style.colors.background),
    ) {
        if (state.settings.lyricTheme == LyricTheme.PICTURE_BG) {
            PictureBackdrop(coverUrl = state.coverUrl, overlay = style.colors.overlay)
        }
        Column(modifier = Modifier.fillMaxSize()) {
            VisualModeMarkers(settings = state.settings)
            if (state.hasSong && !state.isLoading) {
                LyricCardOpenRow(
                    textColor = style.colors.text,
                    onLyricCardClick = onLyricCardClick,
                )
            }
            if (state.frame != null) {
                OffsetRow(
                    offsetMs = state.userOffsetMs,
                    onNudgeOffset = onNudgeOffset,
                    textColor = style.colors.text,
                )
            }
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                LyricsBody(
                    state = state,
                    style = style,
                    onSeekToLine = onSeekToLine,
                )
            }
        }
    }
}

@Composable
private fun PictureBackdrop(
    coverUrl: String?,
    overlay: Color,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (coverUrl != null) {
            val blurModifier =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.blur(20.dp)
                } else {
                    Modifier
                }
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .then(blurModifier)
                        .testTag(LyricsTestTags.COVER_BACKDROP),
                contentScale = ContentScale.Crop,
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(overlay),
        )
    }
}

@Composable
private fun VisualModeMarkers(settings: PlayerSettings) {
    Box(modifier = Modifier.testTag(LyricsTestTags.align(settings.lyricAlign)))
    Box(modifier = Modifier.testTag(LyricsTestTags.theme(settings.lyricTheme)))
    if (settings.karaokeMode) {
        Box(modifier = Modifier.testTag(LyricsTestTags.KARAOKE))
    }
    if (settings.blurMode) {
        Box(modifier = Modifier.testTag(LyricsTestTags.BLUR))
    }
    if (settings.giganticLine) {
        Box(modifier = Modifier.testTag(LyricsTestTags.GIGANTIC))
    }
    if (settings.wordAppear) {
        Box(modifier = Modifier.testTag(LyricsTestTags.WORD_APPEAR))
    }
    if (settings.lineAppear) {
        Box(modifier = Modifier.testTag(LyricsTestTags.LINE_APPEAR))
    }
    if (settings.translate) {
        Box(modifier = Modifier.testTag(LyricsTestTags.TRANSLATE))
    }
}

@Composable
private fun LyricsBody(
    state: LyricsUiState,
    style: LyricVisualStyle,
    onSeekToLine: (Long) -> Unit,
) {
    when {
        !state.hasSong -> Unit
        state.isLoading -> {
            Text(
                text = stringResource(R.string.lyrics_fetching),
                color = style.colors.text,
                modifier = Modifier.testTag(LyricsTestTags.FETCHING),
            )
        }
        state.isMissing -> {
            MissingLyricMessage(style = style)
        }
        state.errorMessage != null -> {
            Text(
                text = stringResource(R.string.lyrics_error),
                color = style.colors.text,
                modifier = Modifier.testTag(LyricsTestTags.ERROR),
            )
        }
        state.frame != null -> {
            SyncedLyricList(
                frame = state.frame,
                style = style,
                onSeekToLine = onSeekToLine,
                clock =
                    KaraokeClock(
                        sampledPositionMs = state.positionMs,
                        sampledAtElapsedMs = state.positionSampledAtElapsedMs,
                        speed = state.speed,
                        isPlaying = state.isPlaying,
                        offsetMs = state.frame.combinedOffsetMs,
                    ),
            )
        }
    }
}

@Composable
private fun MissingLyricMessage(style: LyricVisualStyle) {
    val uriHandler = LocalUriHandler.current
    val intro = stringResource(R.string.lyrics_missing_intro)
    val mail = stringResource(R.string.lyrics_missing_mail)
    val fb = stringResource(R.string.lyrics_missing_fb)
    val mailUri = stringResource(R.string.lyrics_missing_mail_uri)
    val fbUri = stringResource(R.string.lyrics_missing_fb_uri)
    Column(
        modifier =
            Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .testTag(LyricsTestTags.MISSING),
    ) {
        Text(
            text = intro,
            color = style.colors.text,
            textAlign = style.textAlign,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = mail,
            color = style.colors.karaokeFill,
            textAlign = style.textAlign,
            textDecoration = TextDecoration.Underline,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(LyricsTestTags.MISSING_MAIL)
                    .clickable { uriHandler.openUri(mailUri) },
        )
        Text(
            text = fb,
            color = style.colors.karaokeFill,
            textAlign = style.textAlign,
            textDecoration = TextDecoration.Underline,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(LyricsTestTags.MISSING_FB)
                    .clickable { uriHandler.openUri(fbUri) },
        )
    }
}

@Composable
private fun LyricCardOpenRow(
    textColor: Color,
    onLyricCardClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        LiliIconButton(
            icon = Icons.Filled.Image,
            label = stringResource(R.string.lyric_card_open),
            onClick = onLyricCardClick,
            modifier = Modifier.testTag(LyricCardTestTags.OPEN),
            tint = textColor,
        )
    }
}

@Composable
private fun OffsetRow(
    offsetMs: Long,
    onNudgeOffset: (Long) -> Unit,
    textColor: Color,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiliIconButton(
            icon = Icons.Filled.Remove,
            label = stringResource(R.string.lyrics_offset_earlier),
            onClick = { onNudgeOffset(-LyricSynchronizer.USER_OFFSET_STEP_MS) },
            modifier = Modifier.testTag(LyricsTestTags.OFFSET_EARLIER),
            tint = textColor,
        )
        Text(
            text = stringResource(R.string.lyrics_offset_value, offsetMs),
            color = textColor,
            modifier = Modifier.testTag(LyricsTestTags.OFFSET_VALUE),
        )
        LiliIconButton(
            icon = Icons.Filled.Add,
            label = stringResource(R.string.lyrics_offset_later),
            onClick = { onNudgeOffset(LyricSynchronizer.USER_OFFSET_STEP_MS) },
            modifier = Modifier.testTag(LyricsTestTags.OFFSET_LATER),
            tint = textColor,
        )
    }
}

@Composable
private fun SyncedLyricList(
    frame: LyricSyncFrame,
    style: LyricVisualStyle,
    onSeekToLine: (Long) -> Unit,
    clock: KaraokeClock,
) {
    val listState = rememberLazyListState()
    val displayPosition = rememberKaraokeDisplayPositionState(clock)
    LaunchedEffect(frame.activeLineIndex) {
        if (frame.activeLineIndex >= 0) {
            listState.animateScrollToItem(frame.activeLineIndex)
        }
    }
    val seekDescription = stringResource(R.string.lyrics_line_cd)
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = style.horizontal,
    ) {
        itemsIndexed(
            items = frame.lines,
            key = { index, _ -> index },
        ) { index, line ->
            LyricLineRow(
                line = line,
                isActive = index == frame.activeLineIndex,
                style = style,
                displayPosition = displayPosition,
                clock = clock,
                onSeek = { onSeekToLine(line.seekPositionMs) },
                seekDescription = seekDescription,
                modifier = Modifier.fillParentMaxWidth(),
            )
        }
        val meta = LyricMetaFooter.content(frame.meta)
        if (!meta.isEmpty) {
            item {
                LyricMetaBlock(content = meta, style = style)
            }
        }
    }
}

@Composable
private fun LyricMetaBlock(
    content: LyricMetaFooter.Content,
    style: LyricVisualStyle,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .testTag(LyricsTestTags.META_FOOTER),
        horizontalAlignment = style.horizontal,
    ) {
        Text(
            text = stringResource(R.string.lyrics_meta_separator),
            color = style.colors.text.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = style.textAlign,
        )
        content.titleLine?.let { title ->
            Text(
                text = title,
                color = style.colors.text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = style.textAlign,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        content.by?.let { by ->
            Text(
                text = stringResource(R.string.lyrics_meta_creator, by),
                color = style.colors.text.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = style.textAlign,
            )
        }
        content.source?.let { source ->
            Text(
                text = stringResource(R.string.lyrics_meta_source, source),
                color = style.colors.text.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = style.textAlign,
            )
        }
        content.date?.let { date ->
            Text(
                text = stringResource(R.string.lyrics_meta_date, date),
                color = style.colors.text.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = style.textAlign,
            )
        }
    }
}

@Composable
private fun LyricLineRow(
    line: LyricLineFrame,
    isActive: Boolean,
    style: LyricVisualStyle,
    displayPosition: MutableLongState,
    clock: KaraokeClock,
    onSeek: () -> Unit,
    seekDescription: String,
    modifier: Modifier = Modifier,
) {
    val lineAlpha = if (style.settings.lineAppear && !isActive) 0f else 1f
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onSeek)
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .graphicsLayer { alpha = lineAlpha }
                .testTag(if (isActive) LyricsTestTags.ACTIVE_LINE else LyricsTestTags.LINE)
                .semantics { contentDescription = seekDescription },
        horizontalAlignment = style.horizontal,
    ) {
        KaraokeLine(
            line = line,
            isActive = isActive,
            style = style,
            displayPosition = displayPosition,
            offsetMs = clock.offsetMs,
            isPlaying = clock.isPlaying,
        )
        if (style.settings.translate) {
            line.translations.forEach { translation ->
                Text(
                    text = translation.text,
                    color = style.colors.text.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    textAlign = style.textAlign,
                    modifier = Modifier.testTag(LyricsTestTags.TRANSLATION),
                )
            }
        }
    }
}
