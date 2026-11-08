package com.anhtuta.lilimusic

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.anhtuta.lilimusic.core.model.SongDeepLink
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySongDeepLink(intent)
        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.auto(
                    lightScrim = Color.TRANSPARENT,
                    darkScrim = Color.TRANSPARENT,
                ),
            navigationBarStyle =
                SystemBarStyle.auto(
                    lightScrim = Color.argb(0xE6, 0xFF, 0xFF, 0xFF),
                    darkScrim = Color.argb(0x80, 0x1B, 0x1B, 0x1B),
                ),
        )
        setContent {
            LiliApp()
        }
    }

    override fun onNewIntent(intent: Intent) {
        applySongDeepLink(intent)
        super.onNewIntent(intent)
        setIntent(intent)
    }

    companion object {
        fun applySongDeepLink(intent: Intent?) {
            val raw = intent?.data?.toString() ?: return
            val canonical = SongDeepLink.canonicalViewUri(raw) ?: return
            intent.data = Uri.parse(canonical)
        }
    }
}
