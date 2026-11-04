package com.nenmongvietnam.lilimusic.core.model

object ImageUrlResolver {
    fun resolve(
        imageUrl: String?,
        baseUrl: String,
    ): String? {
        val trimmed = imageUrl?.trim().orEmpty()
        if (trimmed.isEmpty()) return null
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed
        }
        val base = baseUrl.trimEnd('/')
        val path = if (trimmed.startsWith("/")) trimmed else "/$trimmed"
        return base + path
    }
}
