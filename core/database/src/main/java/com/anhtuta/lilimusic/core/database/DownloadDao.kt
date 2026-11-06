package com.anhtuta.lilimusic.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY updatedAtEpochMs DESC")
    fun observeAll(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE songPath = :songPath LIMIT 1")
    fun observeByPath(songPath: String): Flow<DownloadEntity?>

    @Query("SELECT * FROM downloads WHERE songPath = :songPath LIMIT 1")
    suspend fun getByPath(songPath: String): DownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE songPath = :songPath")
    suspend fun deleteByPath(songPath: String)

    @Query("DELETE FROM downloads")
    suspend fun deleteAll()
}
