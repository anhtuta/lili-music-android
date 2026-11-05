package com.anhtuta.lilimusic.feature.lyrics

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.lyrics.LyricLineFrame
import com.anhtuta.lilimusic.core.lyrics.LyricSyncFrame
import com.anhtuta.lilimusic.core.lyrics.LyricSynchronizer
import com.anhtuta.lilimusic.core.lyrics.LyricWordFrame

private val KaraokeFillLight = Color(0xFF2199F9)
private val KaraokeFillDark = Color(0xFFFFCC00)

@Composable
fun LyricsPane(
    state: LyricsUiState,
    onNudgeOffset: (Long) -> Unit,
    onSeekToLine: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .testTag(LyricsTestTags.PANE),
    ) {
        if (state.frame != null) {
            OffsetRow(
                offsetMs = state.userOffsetMs,
                onNudgeOffset = onNudgeOffset,
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
                onSeekToLine = onSeekToLine,
            )
        }
    }
}

@Composable
private fun LyricsBody(
    state: LyricsUiState,
    onSeekToLine: (Long) -> Unit,
) {
    when {
        !state.hasSong -> Unit
        state.isLoading -> {
            Text(
                text = stringResource(R.string.lyrics_fetching),
                modifier = Modifier.testTag(LyricsTestTags.FETCHING),
            )
        }
        state.isMissing -> {
            Text(
                text = stringResource(R.string.lyrics_missing),
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .padding(24.dp)
                        .testTag(LyricsTestTags.MISSING),
            )
        }
        state.errorMessage != null -> {
            Text(
                text = stringResource(R.string.lyrics_error),
                modifier = Modifier.testTag(LyricsTestTags.ERROR),
            )
        }
        state.frame != null -> {
            SyncedLyricList(
                frame = state.frame,
                onSeekToLine = onSeekToLine,
            )
        }
    }
}

@Composable
private fun OffsetRow(
    offsetMs: Long,
    onNudgeOffset: (Long) -> Unit,
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
            Text("−100ms")
        }
        Text(
            text = stringResource(R.string.lyrics_offset_value, offsetMs),
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
            Text("+100ms")
        }
    }
}

@Composable
private fun SyncedLyricList(
    frame: LyricSyncFrame,
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
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(frame.lines) { index, line ->
            LyricLineRow(
                line = line,
                isActive = index == frame.activeLineIndex,
                onSeek = { onSeekToLine(line.seekPositionMs) },
                seekDescription = seekDescription,
            )
        }
    }
}

@Composable
private fun LyricLineRow(
    line: LyricLineFrame,
    isActive: Boolean,
    onSeek: () -> Unit,
    seekDescription: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onSeek)
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .testTag(if (isActive) LyricsTestTags.ACTIVE_LINE else LyricsTestTags.LINE)
                .semantics { contentDescription = seekDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            line.words.forEach { word ->
                KaraokeWord(word = word, emphasize = isActive)
            }
        }
        line.translations.forEach { translation ->
            Text(
                text = translation.text,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.testTag(LyricsTestTags.TRANSLATION),
            )
        }
    }
}

@Composable
private fun KaraokeWord(
    word: LyricWordFrame,
    emphasize: Boolean,
) {
    val displayText =
        when {
            word.isEmpty && word.countdownDots > 0 -> "•".repeat(word.countdownDots)
            word.isEmpty -> " "
            else -> word.text
        }
    val fillColor = if (MaterialTheme.colorScheme.background.luminanceOrLight()) KaraokeFillLight else KaraokeFillDark
    val restColor = MaterialTheme.colorScheme.onSurface
    val brush =
        when {
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
                fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
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

private fun Color.luminanceOrLight(): Boolean {
    val luminance = (0.299f * red) + (0.587f * green) + (0.114f * blue)
    return luminance > 0.5f
}
