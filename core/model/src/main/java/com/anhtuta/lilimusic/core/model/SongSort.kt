package com.anhtuta.lilimusic.core.model

enum class SortOrder(
    val apiValue: String,
) {
    ASC("ASC"),
    DESC("DESC"),
    ;

    fun toggle(): SortOrder = if (this == ASC) DESC else ASC
}

/**
 * Whitelist of `/api/song/all` `sortBy` values. Unknown columns cause HTTP 500 on the live API.
 */
enum class SongSort(
    val apiValue: String,
    val defaultOrder: SortOrder,
) {
    TITLE("title", SortOrder.ASC),
    LISTENS("listens", SortOrder.DESC),
    SONG_OF_THE_YEAR("song_of_the_year", SortOrder.DESC),
    TYPE("type", SortOrder.ASC),
    CREATED_DATE("createdDate", SortOrder.DESC),
    ;

    companion object {
        private val byApiValue = entries.associateBy { it.apiValue }

        fun fromApiValue(value: String): SongSort? =
            when (value) {
                "created_date" -> CREATED_DATE
                else -> byApiValue[value]
            }
    }
}
