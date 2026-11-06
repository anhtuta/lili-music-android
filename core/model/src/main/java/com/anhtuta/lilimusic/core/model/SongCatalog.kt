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

    fun filter(
        songs: List<Song>,
        query: String,
    ): List<Song> = songs.filter { matchesQuery(it, query) }

    /**
     * MySQL-style nulls: ASC nulls first, DESC nulls last.
     * Used if a local sort remains (cached catalog when a refresh cannot run).
     */
    fun sorted(
        songs: List<Song>,
        sort: SongSort,
        order: SortOrder,
    ): List<Song> = songs.sortedWith(comparator(sort, order))

    fun comparator(
        sort: SongSort,
        order: SortOrder,
    ): Comparator<Song> {
        val ascending =
            when (sort) {
                SongSort.TITLE -> compareBy(String.CASE_INSENSITIVE_ORDER, Song::title)
                SongSort.LISTENS -> compareBy(Song::listens)
                SongSort.SONG_OF_THE_YEAR -> compareBy(nullsFirst(), Song::songOfTheYear)
                SongSort.TYPE -> compareBy(nullsFirst(String.CASE_INSENSITIVE_ORDER), Song::type)
                SongSort.CREATED_DATE -> compareBy(nullsFirst(), Song::createdDate)
            }
        return if (order == SortOrder.DESC) ascending.reversed() else ascending
    }
}
