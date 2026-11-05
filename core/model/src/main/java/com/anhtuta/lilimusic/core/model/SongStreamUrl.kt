package com.anhtuta.lilimusic.core.model

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object SongStreamUrl {
    fun build(
        baseUrl: String,
        fileName: String,
    ): String {
        require(fileName.isNotBlank()) { "fileName must be non-blank to build a stream URL" }
        val encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20")
        return "${baseUrl.trimEnd('/')}/api/song/file?file=$encoded"
    }
}
