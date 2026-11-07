package com.anhtuta.lilimusic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.anhtuta.lilimusic.core.model.SongDeepLink
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySongDeepLink(intent)
        enableEdgeToEdge()
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
