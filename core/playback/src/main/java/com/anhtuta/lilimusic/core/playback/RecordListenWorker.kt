package com.anhtuta.lilimusic.core.playback

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.anhtuta.lilimusic.core.data.repository.SongRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RecordListenWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val songRepository: SongRepository,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result = RecordListenWork.run(inputData.getString(KEY_PATH), songRepository::recordListen)

        companion object {
            const val KEY_PATH = "path"
        }
    }

object RecordListenWork {
    suspend fun run(
        path: String?,
        recordListen: suspend (String) -> kotlin.Result<Unit>,
    ): ListenableWorker.Result {
        if (path.isNullOrBlank()) {
            return ListenableWorker.Result.failure()
        }
        return recordListen(path).fold(
            onSuccess = { ListenableWorker.Result.success() },
            onFailure = { ListenableWorker.Result.retry() },
        )
    }
}
