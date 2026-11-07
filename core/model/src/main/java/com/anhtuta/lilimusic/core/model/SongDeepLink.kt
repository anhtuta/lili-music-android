package com.anhtuta.lilimusic.core.model

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Web HashRouter song URLs are `#/bai-hat/{path}`. Android App Links cannot match
 * fragments, so HTTPS filters use path-style `/bai-hat/{path}` (and the GitHub Pages
 * prefix `/lili-player/bai-hat/{path}`). Incoming VIEW URIs are canonicalised to
 * `lilimusic://bai-hat/{path}` for Navigation.
 */
object SongDeepLink {
    const val CUSTOM_SCHEME = "lilimusic"
    const val SONG_SEGMENT = "bai-hat"
    const val API_HTTPS_HOST = "blog.nenmongvietnam.com"
    const val PLAYER_HTTPS_HOST = "anhtuta.github.io"
    const val GITHUB_PAGES_PREFIX = "/lili-player"

    fun customUri(path: String): String = "$CUSTOM_SCHEME://$SONG_SEGMENT/${path.trim('/')}"

    fun canonicalViewUri(raw: String): String? = songPath(raw)?.let(::customUri)

    fun songPath(raw: String): String? {
        val uri = runCatching { URI(raw.trim()) }.getOrNull() ?: return null
        fragmentSongPath(uri.rawFragment ?: uri.fragment)?.let { return it }
        customSchemePath(uri)?.let { return it }
        httpsPath(uri)?.let { return it }
        return null
    }

    private fun fragmentSongPath(fragment: String?): String? {
        val value = fragment?.trim()?.trimStart('#') ?: return null
        val path = if (value.startsWith("/")) value else "/$value"
        return afterSongPrefix(path)
    }

    private fun customSchemePath(uri: URI): String? {
        if (uri.scheme != CUSTOM_SCHEME) {
            return null
        }
        if (uri.host == SONG_SEGMENT) {
            return decodeSegment(uri.path.orEmpty().trim('/'))
        }
        val combined = "${uri.host.orEmpty()}${uri.path.orEmpty()}"
        return afterSongPrefix("/${combined.trimStart('/')}")
    }

    private fun httpsPath(uri: URI): String? {
        if (uri.scheme != "https" && uri.scheme != "http") {
            return null
        }
        val path = uri.path.orEmpty()
        return when (uri.host) {
            API_HTTPS_HOST -> afterSongPrefix(path)
            PLAYER_HTTPS_HOST -> afterSongPrefix(path.removePrefix(GITHUB_PAGES_PREFIX).ifEmpty { path })
            else -> afterSongPrefix(path)
        }
    }

    private fun afterSongPrefix(path: String): String? {
        val normalised = if (path.startsWith("/")) path else "/$path"
        val prefix = "/$SONG_SEGMENT/"
        if (!normalised.startsWith(prefix)) {
            return null
        }
        return decodeSegment(normalised.removePrefix(prefix).substringBefore('?'))
    }

    private fun decodeSegment(value: String): String? {
        val trimmed = value.trim().trim('/')
        if (trimmed.isEmpty()) {
            return null
        }
        val decoded = URLDecoder.decode(trimmed, StandardCharsets.UTF_8)
        return decoded.takeIf { it.isNotBlank() }
    }
}
