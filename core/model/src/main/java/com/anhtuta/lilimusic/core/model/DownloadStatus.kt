package com.anhtuta.lilimusic.core.model

enum class DownloadStatus {
    NONE,
    QUEUED,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    ;

    val isActive: Boolean
        get() = this == QUEUED || this == DOWNLOADING

    companion object {
        fun fromStorage(value: String?): DownloadStatus =
            entries.find { it.name == value } ?: NONE
    }
}
