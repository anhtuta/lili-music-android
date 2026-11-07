package com.anhtuta.lilimusic.feature.lyrics

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.core.app.ShareCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LyricCardRoute(
    modifier: Modifier = Modifier,
    viewModel: LyricCardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LyricCardMessageEffects(state = state, snackbar = snackbar, viewModel = viewModel)
    LyricCardShareEffect(state = state, viewModel = viewModel)

    val showingCard = state.step != LyricCardStep.HIDDEN
    Box(
        modifier =
            modifier.then(
                if (showingCard) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        if (showingCard) {
            LyricCardScreen(
                state = state,
                callbacks =
                    LyricCardCallbacks(
                        onClose = viewModel::close,
                        onToggleLine = viewModel::toggleLine,
                        onSelectAll = viewModel::selectAll,
                        onGenerate = viewModel::generate,
                        onBackToSelection = viewModel::backToSelection,
                        onShare = viewModel::shareCard,
                        onCopy = viewModel::copySelectedText,
                        onTextAlign = viewModel::setTextAlign,
                        onTextColor = viewModel::setTextColor,
                        onBold = viewModel::setBold,
                        onBackgroundColor = viewModel::setBackgroundColor,
                        onUseSongImage = viewModel::setUseSongImage,
                        onOverlayOpacity = viewModel::setOverlayOpacity,
                        onBrightnessBoost = viewModel::setBrightnessBoost,
                        onFontFamily = viewModel::setFontFamily,
                        onFontSize = viewModel::setFontSize,
                    ),
                modifier = Modifier.fillMaxSize(),
            )
        }
        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.testTag(LyricCardTestTags.NO_LYRIC),
        )
    }
}

@Composable
private fun LyricCardMessageEffects(
    state: LyricCardUiState,
    snackbar: SnackbarHostState,
    viewModel: LyricCardViewModel,
) {
    val noLyric = stringResource(R.string.lyric_card_no_lyric)
    val copied = stringResource(R.string.lyric_card_copied)
    val shareFailed = stringResource(R.string.lyric_card_share_failed)
    LaunchedEffect(state.showNoLyricWarning) {
        if (state.showNoLyricWarning) {
            snackbar.showSnackbar(noLyric)
            viewModel.consumeNoLyricWarning()
        }
    }
    LaunchedEffect(state.copied) {
        if (state.copied) {
            snackbar.showSnackbar(copied)
            viewModel.consumeCopied()
        }
    }
    LaunchedEffect(state.shareFailed) {
        if (state.shareFailed) {
            snackbar.showSnackbar(shareFailed)
            viewModel.consumeShareFailed()
        }
    }
}

@Composable
private fun LyricCardShareEffect(
    state: LyricCardUiState,
    viewModel: LyricCardViewModel,
) {
    val context = LocalContext.current
    val chooser = stringResource(R.string.lyric_card_share_chooser)
    LaunchedEffect(state.shareUri) {
        val uri = state.shareUri ?: return@LaunchedEffect
        val activity = context as? Activity
        if (activity != null) {
            val intent =
                ShareCompat
                    .IntentBuilder(activity)
                    .setType("image/jpeg")
                    .setStream(uri)
                    .setChooserTitle(chooser)
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activity.startActivity(intent)
        }
        viewModel.consumeShare()
    }
}
