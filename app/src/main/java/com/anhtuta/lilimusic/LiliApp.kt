package com.anhtuta.lilimusic

import androidx.compose.runtime.Composable
import com.anhtuta.lilimusic.core.ui.theme.LiliTheme
import com.anhtuta.lilimusic.feature.library.LibraryRoute
import com.anhtuta.lilimusic.feature.player.PlayerPlaceholderScreen
import com.anhtuta.lilimusic.navigation.LiliNavHost

@Composable
fun LiliApp() {
    LiliTheme {
        LiliNavHost(
            libraryContent = { onSongClick ->
                LibraryRoute(onSongClick = { song -> onSongClick(song.id) })
            },
            playerContent = { songId, onBack ->
                PlayerPlaceholderScreen(
                    songId = songId,
                    onBack = onBack,
                )
            },
        )
    }
}
