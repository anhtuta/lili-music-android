package com.anhtuta.lilimusic.feature.lyrics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface LyricCardClipboard {
    fun copy(text: String)
}

@Singleton
class AndroidLyricCardClipboard
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : LyricCardClipboard {
        override fun copy(text: String) {
            val clipboard = context.getSystemService<ClipboardManager>() ?: return
            clipboard.setPrimaryClip(ClipData.newPlainText(LABEL, text))
        }

        private companion object {
            const val LABEL = "lyric"
        }
    }
