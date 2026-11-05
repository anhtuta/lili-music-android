package com.anhtuta.lilimusic.core.database

import androidx.room.withTransaction
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

interface SongLocalDataSource {
    fun observeSongs(): Flow<List<SongEntity>>

    fun observeSong(id: Long): Flow<SongEntity?>

    fun observeSongByPath(path: String): Flow<SongEntity?>

    suspend fun replaceAll(songs: List<SongEntity>)
}

@Singleton
class RoomSongLocalDataSource
    @Inject
    constructor(
        private val database: LiliMusicDatabase,
        private val songDao: SongDao,
    ) : SongLocalDataSource {
        override fun observeSongs(): Flow<List<SongEntity>> = songDao.observeAll()

        override fun observeSong(id: Long): Flow<SongEntity?> = songDao.observeById(id)

        override fun observeSongByPath(path: String): Flow<SongEntity?> = songDao.observeByPath(path)

        override suspend fun replaceAll(songs: List<SongEntity>) {
            database.withTransaction {
                songDao.deleteAll()
                if (songs.isNotEmpty()) {
                    songDao.insertAll(songs)
                }
            }
        }
    }
