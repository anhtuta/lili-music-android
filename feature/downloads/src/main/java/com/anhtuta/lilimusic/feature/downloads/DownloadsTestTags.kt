package com.anhtuta.lilimusic.feature.downloads

object DownloadsTestTags {
    const val SCREEN = "downloads-screen"
    const val BACK = "downloads-back"
    const val EMPTY = "downloads-empty"
    const val LIST = "downloads-list"
    const val DELETE_ALL = "downloads-delete-all"
    const val CONFIRM_DELETE_ALL = "downloads-confirm-delete-all"
    const val CANCEL_DELETE_ALL = "downloads-cancel-delete-all"

    fun row(path: String): String = "downloads-row-$path"

    fun retry(path: String): String = "downloads-retry-$path"

    fun cancel(path: String): String = "downloads-cancel-$path"

    fun delete(path: String): String = "downloads-delete-$path"

    fun play(path: String): String = "downloads-play-$path"

    fun progress(path: String): String = "downloads-progress-$path"
}
