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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anhtuta.lilimusic.core.lyrics.LyricLineFrame
import com.anhtuta.lilimusic.core.lyrics.LyricMetaFooter
import com.anhtuta.lilimusic.core.lyrics.LyricSyncFrame
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.lyrics.LyricWordFrame
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings

@Composable
fun LyricsPane(
    state: LyricsUiState,
    onNudgeOffset: (Long) -> Unit,
    onSeekToLine: (Long) -> Unit,
    modifier: Modifier = Modifier,
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
            Text(
                text = stringResource(R.string.lyrics_missing),
                color = style.colors.text,
                textAlign = style.textAlign,
                modifier =
                    Modifier
                        .padding(24.dp)
                        .testTag(LyricsTestTags.MISSING),
            )
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
            )
        }
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
        val earlier = stringResource(R.string.lyrics_offset_earlier)
        TextButton(
            onClick = { onNudgeOffset(-LyricSynchronizer.USER_OFFSET_STEP_MS) },
            modifier =
                Modifier
                    .testTag(LyricsTestTags.OFFSET_EARLIER)
                    .semantics { contentDescription = earlier },
        ) {
            Text("−100ms", color = textColor)
        }
        Text(
            text = stringResource(R.string.lyrics_offset_value, offsetMs),
            color = textColor,
            modifier = Modifier.testTag(LyricsTestTags.OFFSET_VALUE),
        )
        val later = stringResource(R.string.lyrics_offset_later)
        TextButton(
            onClick = { onNudgeOffset(LyricSynchronizer.USER_OFFSET_STEP_MS) },
            modifier =
                Modifier
                    .testTag(LyricsTestTags.OFFSET_LATER)
                    .semantics { contentDescription = later },
        ) {
            Text("+100ms", color = textColor)
        }
    }
}

@Composable
private fun SyncedLyricList(
    frame: LyricSyncFrame,
    style: LyricVisualStyle,
    onSeekToLine: (Long) -> Unit,
) {
    val listState = rememberLazyListState()
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
        itemsIndexed(frame.lines) { index, line ->
            LyricLineRow(
                line = line,
                isActive = index == frame.activeLineIndex,
                style = style,
                onSeek = { onSeekToLine(line.seekPositionMs) },
                seekDescription = seekDescription,
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
    onSeek: () -> Unit,
    seekDescription: String,
) {
    val lineAlpha = if (style.settings.lineAppear && !isActive) 0f else 1f
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onSeek)
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .graphicsLayer { alpha = lineAlpha }
                .testTag(if (isActive) LyricsTestTags.ACTIVE_LINE else LyricsTestTags.LINE)
                .semantics { contentDescription = seekDescription },
        horizontalAlignment = style.horizontal,
    ) {
        Row {
            line.words.forEach { word ->
                KaraokeWord(word = word, isActiveLine = isActive, style = style)
            }
        }
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

@Composable
private fun KaraokeWord(
    word: LyricWordFrame,
    isActiveLine: Boolean,
    style: LyricVisualStyle,
) {
    val hideWord = (style.settings.wordAppear || style.settings.lineAppear) && !word.isPassed && !word.isActive
    val displayText =
        when {
            hideWord -> ""
            word.isEmpty && word.countdownDots > 0 -> "•".repeat(word.countdownDots)
            word.isEmpty -> " "
            else -> word.text
        }
    val fillColor = style.colors.karaokeFill
    val restColor = if (isActiveLine) style.colors.activeText else style.colors.text
    val brush = wordBrush(word, style.settings.karaokeMode, fillColor, restColor)
    val fontSize =
        MaterialTheme.typography.bodyLarge.fontSize *
            if (style.settings.giganticLine && isActiveLine) 1.8f else 1f
    val countdownDescription =
        if (word.countdownDots > 0) {
            stringResource(R.string.lyrics_countdown_cd, word.countdownDots)
        } else {
            null
        }
    Text(
        text = displayText,
        style =
            TextStyle(
                brush = brush,
                fontWeight = if (isActiveLine) FontWeight.Bold else FontWeight.Normal,
                fontSize = fontSize,
                shadow =
                    if (style.settings.blurMode) {
                        Shadow(color = Color.White, offset = Offset.Zero, blurRadius = 8f)
                    } else {
                        null
                    },
            ),
        modifier =
            Modifier.then(
                if (countdownDescription != null) {
                    Modifier.semantics { contentDescription = countdownDescription }
                } else {
                    Modifier
                },
            ),
    )
}

private fun wordBrush(
    word: LyricWordFrame,
    karaokeMode: Boolean,
    fillColor: Color,
    restColor: Color,
): Brush {
    if (!karaokeMode) {
        return SolidColor(if (word.isActive || word.isPassed) fillColor else restColor)
    }
    return when {
        word.fill <= 0f -> SolidColor(restColor)
        word.fill >= 1f -> SolidColor(fillColor)
        else ->
            Brush.horizontalGradient(
                0f to fillColor,
                word.fill to fillColor,
                word.fill to restColor,
                1f to restColor,
            )
    }
}
