package com.anhtuta.lilimusic.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LyricDao {
    @Query("SELECT * FROM lyrics WHERE fileName = :fileName LIMIT 1")
    suspend fun getByFileName(fileName: String): LyricEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(lyric: LyricEntity)

    @Query("DELETE FROM lyrics WHERE fileName = :fileName")
    suspend fun deleteByFileName(fileName: String)
}
