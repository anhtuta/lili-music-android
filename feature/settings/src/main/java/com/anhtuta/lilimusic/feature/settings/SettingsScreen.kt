package com.anhtuta.lilimusic.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anhtuta.lilimusic.core.model.AppTheme
import com.anhtuta.lilimusic.core.model.LyricAlign
import com.anhtuta.lilimusic.core.model.LyricFontSize
import com.anhtuta.lilimusic.core.model.LyricTheme
import com.anhtuta.lilimusic.core.model.PlayerSettings
import com.anhtuta.lilimusic.core.ui.button.LiliIconButton
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    callbacks: SettingsCallbacks,
    modifier: Modifier = Modifier,
) {
    val settings = state.settings
    Scaffold(
        modifier = modifier.testTag(SettingsTestTags.SCREEN),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    LiliIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        label = stringResource(R.string.settings_back),
                        onClick = callbacks.onBack,
                        modifier = Modifier.testTag(SettingsTestTags.BACK),
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AlignmentSection(align = settings.lyricAlign, onLyricAlign = callbacks.onLyricAlign)
            LyricFontSizeSection(size = settings.lyricFontSize, onLyricFontSize = callbacks.onLyricFontSize)
            LyricThemeSection(theme = settings.lyricTheme, onLyricTheme = callbacks.onLyricTheme)
            ModeSection(state = settings, callbacks = callbacks)
            AppThemeSection(theme = settings.appTheme, onAppTheme = callbacks.onAppTheme)
            SpeedSection(
                speed = state.speed,
                onIncreaseSpeed = callbacks.onIncreaseSpeed,
                onDecreaseSpeed = callbacks.onDecreaseSpeed,
            )
        }
    }
}

@Composable
private fun LyricFontSizeSection(
    size: LyricFontSize,
    onLyricFontSize: (LyricFontSize) -> Unit,
) {
    SettingsLegend(text = stringResource(R.string.settings_lyric_font_size))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LyricFontSize.entries.forEach { option ->
            FilterChip(
                selected = size == option,
                onClick = { onLyricFontSize(option) },
                label = { Text(text = stringResource(lyricFontSizeLabelRes(option))) },
                modifier = Modifier.testTag(SettingsTestTags.fontSize(option)),
            )
        }
    }
}

private fun lyricFontSizeLabelRes(size: LyricFontSize): Int =
    when (size) {
        LyricFontSize.COMPACT -> R.string.settings_font_compact
        LyricFontSize.NORMAL -> R.string.settings_font_normal
        LyricFontSize.COMFORTABLE -> R.string.settings_font_comfortable
        LyricFontSize.LARGE -> R.string.settings_font_large
        LyricFontSize.EXTRA_LARGE -> R.string.settings_font_extra_large
    }

@Composable
private fun AlignmentSection(
    align: LyricAlign,
    onLyricAlign: (LyricAlign) -> Unit,
) {
    SettingsLegend(text = stringResource(R.string.settings_lyric_align))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = align == LyricAlign.LEFT,
            onClick = { onLyricAlign(LyricAlign.LEFT) },
            label = { Text(stringResource(R.string.settings_align_left)) },
            modifier = Modifier.testTag(SettingsTestTags.ALIGN_LEFT),
        )
        FilterChip(
            selected = align == LyricAlign.CENTER,
            onClick = { onLyricAlign(LyricAlign.CENTER) },
            label = { Text(stringResource(R.string.settings_align_center)) },
            modifier = Modifier.testTag(SettingsTestTags.ALIGN_CENTER),
        )
        FilterChip(
            selected = align == LyricAlign.RIGHT,
            onClick = { onLyricAlign(LyricAlign.RIGHT) },
            label = { Text(stringResource(R.string.settings_align_right)) },
            modifier = Modifier.testTag(SettingsTestTags.ALIGN_RIGHT),
        )
    }
}

@Composable
private fun LyricThemeSection(
    theme: LyricTheme,
    onLyricTheme: (LyricTheme) -> Unit,
) {
    SettingsLegend(text = stringResource(R.string.settings_lyric_theme))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = theme == LyricTheme.DARK,
            onClick = { onLyricTheme(LyricTheme.DARK) },
            label = { Text(stringResource(R.string.settings_theme_dark)) },
            modifier = Modifier.testTag(SettingsTestTags.THEME_DARK),
        )
        FilterChip(
            selected = theme == LyricTheme.LIGHT,
            onClick = { onLyricTheme(LyricTheme.LIGHT) },
            label = { Text(stringResource(R.string.settings_theme_light)) },
            modifier = Modifier.testTag(SettingsTestTags.THEME_LIGHT),
        )
        FilterChip(
            selected = theme == LyricTheme.PICTURE_BG,
            onClick = { onLyricTheme(LyricTheme.PICTURE_BG) },
            label = { Text(stringResource(R.string.settings_theme_picture_bg)) },
            modifier = Modifier.testTag(SettingsTestTags.THEME_PICTURE_BG),
        )
    }
}

@Composable
private fun ModeSection(
    state: PlayerSettings,
    callbacks: SettingsCallbacks,
) {
    SettingsLegend(text = stringResource(R.string.settings_mode))
    SettingsSwitch(
        label = stringResource(R.string.settings_karaoke),
        checked = state.karaokeMode,
        onCheckedChange = callbacks.onKaraokeMode,
        testTag = SettingsTestTags.KARAOKE,
    )
    SettingsSwitch(
        label = stringResource(R.string.settings_blur),
        checked = state.blurMode,
        onCheckedChange = callbacks.onBlurMode,
        testTag = SettingsTestTags.BLUR,
    )
    SettingsSwitch(
        label = stringResource(R.string.settings_word_appear),
        checked = state.wordAppear,
        onCheckedChange = callbacks.onWordAppear,
        testTag = SettingsTestTags.WORD_APPEAR,
    )
    SettingsSwitch(
        label = stringResource(R.string.settings_line_appear),
        checked = state.lineAppear,
        onCheckedChange = callbacks.onLineAppear,
        testTag = SettingsTestTags.LINE_APPEAR,
    )
    SettingsSwitch(
        label = stringResource(R.string.settings_gigantic),
        checked = state.giganticLine,
        onCheckedChange = callbacks.onGiganticLine,
        testTag = SettingsTestTags.GIGANTIC,
    )
    SettingsSwitch(
        label = stringResource(R.string.settings_translate),
        checked = state.translate,
        onCheckedChange = callbacks.onTranslate,
        testTag = SettingsTestTags.TRANSLATE,
    )
}

@Composable
private fun AppThemeSection(
    theme: AppTheme,
    onAppTheme: (AppTheme) -> Unit,
) {
    SettingsLegend(text = stringResource(R.string.settings_app_theme))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = theme == AppTheme.LIGHT,
            onClick = { onAppTheme(AppTheme.LIGHT) },
            label = { Text(stringResource(R.string.settings_app_light)) },
            modifier = Modifier.testTag(SettingsTestTags.APP_THEME_LIGHT),
        )
        FilterChip(
            selected = theme == AppTheme.DARK,
            onClick = { onAppTheme(AppTheme.DARK) },
            label = { Text(stringResource(R.string.settings_app_dark)) },
            modifier = Modifier.testTag(SettingsTestTags.APP_THEME_DARK),
        )
    }
}

@Composable
private fun SpeedSection(
    speed: Float,
    onIncreaseSpeed: () -> Unit,
    onDecreaseSpeed: () -> Unit,
) {
    SettingsLegend(text = stringResource(R.string.settings_speed))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LiliIconButton(
            icon = Icons.Filled.Remove,
            label = stringResource(R.string.settings_speed_down),
            onClick = onDecreaseSpeed,
            modifier = Modifier.testTag(SettingsTestTags.SPEED_DOWN),
        )
        Text(
            text =
                stringResource(
                    R.string.settings_speed_value,
                    String.format(Locale.US, "%.1f", speed),
                ),
            modifier = Modifier.testTag(SettingsTestTags.SPEED_VALUE),
        )
        LiliIconButton(
            icon = Icons.Filled.Add,
            label = stringResource(R.string.settings_speed_up),
            onClick = onIncreaseSpeed,
            modifier = Modifier.testTag(SettingsTestTags.SPEED_UP),
        )
    }
}

@Composable
private fun SettingsLegend(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun SettingsSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
        )
    }
}
