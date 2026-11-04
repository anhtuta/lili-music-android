package com.nenmongvietnam.lilimusic

import androidx.compose.runtime.Composable
import com.nenmongvietnam.lilimusic.core.ui.theme.LiliTheme
import com.nenmongvietnam.lilimusic.feature.library.LibraryRoute
import com.nenmongvietnam.lilimusic.feature.player.PlayerPlaceholderScreen
import com.nenmongvietnam.lilimusic.navigation.LiliNavHost

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
