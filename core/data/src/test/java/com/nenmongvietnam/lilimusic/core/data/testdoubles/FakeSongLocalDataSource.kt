package com.nenmongvietnam.lilimusic.core.data.testdoubles

import com.nenmongvietnam.lilimusic.core.database.SongEntity
import com.nenmongvietnam.lilimusic.core.database.SongLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSongLocalDataSource : SongLocalDataSource {
    private val songs = MutableStateFlow<List<SongEntity>>(emptyList())

    override fun observeSongs(): Flow<List<SongEntity>> = songs

    override fun observeSong(id: Long): Flow<SongEntity?> = songs.map { list -> list.find { it.id == id } }

    override suspend fun replaceAll(songs: List<SongEntity>) {
        this.songs.value = songs
    }

    fun seed(value: List<SongEntity>) {
        songs.value = value
    }
}
