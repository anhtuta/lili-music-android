package com.anhtuta.lilimusic.core.model

object SongCatalog {
    fun isPlayableFileName(fileName: String?): Boolean = !fileName.isNullOrBlank()

    fun matchesQuery(
        song: Song,
        query: String,
    ): Boolean {
        val needle = query.trim()
        if (needle.isEmpty()) return true
        return song.title.contains(needle, ignoreCase = true) ||
            song.artist.contains(needle, ignoreCase = true)
    }

    fun filterAndSort(
        songs: List<Song>,
        query: String,
        sort: SongSort,
        order: SortOrder,
    ): List<Song> = songs.filter { matchesQuery(it, query) }.sortedWith(comparator(sort, order))

    fun comparator(
        sort: SongSort,
        order: SortOrder,
    ): Comparator<Song> {
        val ascending =
            when (sort) {
                SongSort.TITLE -> compareBy(String.CASE_INSENSITIVE_ORDER, Song::title)
                SongSort.LISTENS -> compareBy(Song::listens)
                SongSort.SONG_OF_THE_YEAR -> compareBy(nullsLast(), Song::songOfTheYear)
                SongSort.TYPE -> compareBy(nullsLast(String.CASE_INSENSITIVE_ORDER), Song::type)
                SongSort.CREATED_DATE -> compareBy(nullsLast(), Song::createdDate)
            }
        return if (order == SortOrder.DESC) ascending.reversed() else ascending
    }
}
