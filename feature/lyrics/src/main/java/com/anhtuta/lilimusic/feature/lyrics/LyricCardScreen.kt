package com.anhtuta.lilimusic.feature.lyrics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricCardFont
import com.anhtuta.lilimusic.core.model.LyricCardSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricCardScreen(
    state: LyricCardUiState,
    callbacks: LyricCardCallbacks,
    modifier: Modifier = Modifier,
) {
    when (state.step) {
        LyricCardStep.HIDDEN -> Unit
        LyricCardStep.SELECT ->
            LyricCardSelectionScreen(state = state, callbacks = callbacks, modifier = modifier)
        LyricCardStep.CONFIGURE ->
            LyricCardConfigureScreen(state = state, callbacks = callbacks, modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LyricCardSelectionScreen(
    state: LyricCardUiState,
    callbacks: LyricCardCallbacks,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.testTag(LyricCardTestTags.SELECTION),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lyric_card_select_title)) },
                navigationIcon = {
                    TextButton(
                        onClick = callbacks.onClose,
                        modifier = Modifier.testTag(LyricCardTestTags.CLOSE),
                    ) {
                        Text(stringResource(R.string.lyric_card_close))
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = stringResource(R.string.lyric_card_select_instruction))
            TextButton(
                onClick = callbacks.onSelectAll,
                modifier = Modifier.testTag(LyricCardTestTags.SELECT_ALL),
            ) {
                Text(stringResource(R.string.lyric_card_select_all))
            }
            state.lines.forEach { line ->
                Text(
                    text = line.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color =
                        if (line.selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .testTag(LyricCardTestTags.line(line.id))
                            .clickable { callbacks.onToggleLine(line.id) }
                            .padding(vertical = 6.dp),
                )
            }
            if (state.selectedText.isNotBlank()) {
                Text(text = stringResource(R.string.lyric_card_selected))
                Text(
                    text = state.selectedText,
                    modifier = Modifier.testTag(LyricCardTestTags.SELECTED_PREVIEW),
                )
                TextButton(
                    onClick = callbacks.onGenerate,
                    modifier = Modifier.testTag(LyricCardTestTags.GENERATE),
                ) {
                    Text(stringResource(R.string.lyric_card_generate))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LyricCardConfigureScreen(
    state: LyricCardUiState,
    callbacks: LyricCardCallbacks,
    modifier: Modifier = Modifier,
) {
    val config = state.config
    Scaffold(
        modifier = modifier.testTag(LyricCardTestTags.CONFIGURE),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lyric_card_configure_title)) },
                navigationIcon = {
                    TextButton(
                        onClick = callbacks.onBackToSelection,
                        modifier = Modifier.testTag(LyricCardTestTags.BACK),
                    ) {
                        Text(stringResource(R.string.lyric_card_back))
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BackgroundSourceRow(config = config, onUseSongImage = callbacks.onUseSongImage)
            AlignRow(align = config.textAlign, onTextAlign = callbacks.onTextAlign)
            TextColorRow(
                selected = config.textColor,
                isBold = config.isBold,
                onTextColor = callbacks.onTextColor,
                onBold = callbacks.onBold,
            )
            BackgroundPresetRow(
                selected = config.backgroundColor,
                onBackgroundColor = callbacks.onBackgroundColor,
            )
            FontRow(selected = config.fontFamily, onFontFamily = callbacks.onFontFamily)
            ConfigureSliders(config = config, callbacks = callbacks)
            ConfigurePreviewAndShare(state = state, callbacks = callbacks)
        }
    }
}

@Composable
private fun ConfigureSliders(
    config: LyricCardSettings,
    callbacks: LyricCardCallbacks,
) {
    LabeledSlider(
        label = stringResource(R.string.lyric_card_font_size, config.fontSize),
        value = config.fontSize.toFloat(),
        range = LyricCardSettings.MIN_FONT_SIZE.toFloat()..LyricCardSettings.MAX_FONT_SIZE.toFloat(),
        steps = LyricCardSettings.MAX_FONT_SIZE - LyricCardSettings.MIN_FONT_SIZE - 1,
        testTag = LyricCardTestTags.FONT_SIZE,
        onValue = { callbacks.onFontSize(it.toInt()) },
    )
    LabeledSlider(
        label = stringResource(R.string.lyric_card_overlay, config.overlayOpacity),
        value = config.overlayOpacity.toFloat(),
        range = LyricCardSettings.MIN_OVERLAY.toFloat()..LyricCardSettings.MAX_OVERLAY.toFloat(),
        steps =
            (LyricCardSettings.MAX_OVERLAY - LyricCardSettings.MIN_OVERLAY) /
                LyricCardSettings.OVERLAY_STEP - 1,
        testTag = LyricCardTestTags.OVERLAY,
        onValue = { callbacks.onOverlayOpacity(it.toInt()) },
    )
    LabeledSlider(
        label = stringResource(R.string.lyric_card_brightness, config.brightnessBoost),
        value = config.brightnessBoost.toFloat(),
        range = LyricCardSettings.MIN_BRIGHTNESS.toFloat()..LyricCardSettings.MAX_BRIGHTNESS.toFloat(),
        steps =
            (LyricCardSettings.MAX_BRIGHTNESS - LyricCardSettings.MIN_BRIGHTNESS) /
                LyricCardSettings.BRIGHTNESS_STEP - 1,
        testTag = LyricCardTestTags.BRIGHTNESS,
        onValue = { callbacks.onBrightnessBoost(it.toInt()) },
        enabled = config.useSongImage,
    )
}

@Composable
private fun ConfigurePreviewAndShare(
    state: LyricCardUiState,
    callbacks: LyricCardCallbacks,
) {
    Text(
        text = state.selectedText,
        fontFamily = LyricCardFontResources.composeFamily(state.config.fontFamily),
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp)
                .testTag(LyricCardTestTags.PREVIEW)
                .padding(16.dp),
    )
    Text(text = state.songTitle, style = MaterialTheme.typography.titleMedium)
    Text(text = state.songArtist, style = MaterialTheme.typography.bodyMedium)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(
            onClick = callbacks.onShare,
            modifier = Modifier.testTag(LyricCardTestTags.SHARE),
        ) {
            Text(stringResource(R.string.lyric_card_share))
        }
        TextButton(
            onClick = callbacks.onCopy,
            modifier = Modifier.testTag(LyricCardTestTags.COPY),
        ) {
            Text(stringResource(R.string.lyric_card_copy))
        }
    }
}

@Composable
private fun BackgroundSourceRow(
    config: LyricCardSettings,
    onUseSongImage: (Boolean) -> Unit,
) {
    Text(text = stringResource(R.string.lyric_card_bg_source))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = !config.useSongImage,
            onClick = { onUseSongImage(false) },
            label = { Text(stringResource(R.string.lyric_card_bg_color)) },
            modifier = Modifier.testTag(LyricCardTestTags.BG_COLOR),
        )
        FilterChip(
            selected = config.useSongImage,
            onClick = { onUseSongImage(true) },
            label = { Text(stringResource(R.string.lyric_card_bg_image)) },
            modifier = Modifier.testTag(LyricCardTestTags.BG_IMAGE),
        )
    }
}

@Composable
private fun AlignRow(
    align: LyricAlign,
    onTextAlign: (LyricAlign) -> Unit,
) {
    Text(text = stringResource(R.string.lyric_card_align))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = align == LyricAlign.LEFT,
            onClick = { onTextAlign(LyricAlign.LEFT) },
            label = { Text(stringResource(R.string.lyric_card_align_left)) },
            modifier = Modifier.testTag(LyricCardTestTags.align("left")),
        )
        FilterChip(
            selected = align == LyricAlign.CENTER,
            onClick = { onTextAlign(LyricAlign.CENTER) },
            label = { Text(stringResource(R.string.lyric_card_align_center)) },
            modifier = Modifier.testTag(LyricCardTestTags.align("center")),
        )
        FilterChip(
            selected = align == LyricAlign.RIGHT,
            onClick = { onTextAlign(LyricAlign.RIGHT) },
            label = { Text(stringResource(R.string.lyric_card_align_right)) },
            modifier = Modifier.testTag(LyricCardTestTags.align("right")),
        )
    }
}

@Composable
private fun TextColorRow(
    selected: String,
    isBold: Boolean,
    onTextColor: (String) -> Unit,
    onBold: (Boolean) -> Unit,
) {
    Text(text = stringResource(R.string.lyric_card_text_color))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf("#ffffff", "#000000", "#ffcc00", "#2199f9", "#e94560").forEach { hex ->
            FilterChip(
                selected = selected == hex,
                onClick = { onTextColor(hex) },
                label = { Text(hex) },
                modifier = Modifier.testTag(LyricCardTestTags.textColor(hex)),
            )
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(R.string.lyric_card_bold))
        Switch(
            checked = isBold,
            onCheckedChange = onBold,
            modifier = Modifier.testTag(LyricCardTestTags.BOLD),
        )
    }
}

@Composable
private fun BackgroundPresetRow(
    selected: String,
    onBackgroundColor: (String) -> Unit,
) {
    Text(text = stringResource(R.string.lyric_card_background_color))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        LyricCardSettings.backgroundPresets.forEach { (hex, _) ->
            FilterChip(
                selected = selected == hex,
                onClick = { onBackgroundColor(hex) },
                label = { Text(hex.removePrefix("#").take(3)) },
                modifier = Modifier.testTag(LyricCardTestTags.backgroundPreset(hex)),
            )
        }
    }
}

@Composable
private fun FontRow(
    selected: LyricCardFont,
    onFontFamily: (LyricCardFont) -> Unit,
) {
    Text(text = stringResource(R.string.lyric_card_font))
    Column {
        LyricCardFont.entries.forEach { font ->
            val family: FontFamily = LyricCardFontResources.composeFamily(font)
            FilterChip(
                selected = selected == font,
                onClick = { onFontFamily(font) },
                label = { Text(text = font.storageValue, fontFamily = family) },
                modifier = Modifier.testTag(LyricCardTestTags.font(font.storageValue)),
            )
        }
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    testTag: String,
    onValue: (Float) -> Unit,
    enabled: Boolean = true,
) {
    Text(text = label)
    Slider(
        value = value,
        onValueChange = onValue,
        valueRange = range,
        steps = steps.coerceAtLeast(0),
        enabled = enabled,
        modifier = Modifier.testTag(testTag),
    )
}
