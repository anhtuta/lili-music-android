package com.anhtuta.lilimusic.feature.lyrics

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface LyricCardFiles {
    fun writeJpeg(
        bytes: ByteArray,
        fileName: String,
    ): Uri
}

@Singleton
class FileProviderLyricCardFiles
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : LyricCardFiles {
        override fun writeJpeg(
            bytes: ByteArray,
            fileName: String,
        ): Uri {
            val dir = File(context.cacheDir, DIR)
            dir.mkdirs()
            val file = File(dir, fileName)
            file.writeBytes(bytes)
            return FileProvider.getUriForFile(context, "${context.packageName}$AUTHORITY_SUFFIX", file)
        }

        companion object {
            const val DIR = "lyric-cards"
            const val AUTHORITY_SUFFIX = ".fileprovider"
        }
    }
