# PR #14 — Phase 1: data layer + library

[Learning guide index](README.md) · [PR #14](https://github.com/anhtuta/lili-music-android/pull/14)

**What was built.** Song model (playable-only), Retrofit `GET /api/song/all`, Room cache, DataStore sort prefs, `OfflineFirstSongRepository`, Library screen (search/sort/pull-to-refresh), Nav shell. Package renamed to `com.anhtuta.lilimusic` (task [005](../tasks/005-rename-package.md)). Task [003](../tasks/003-data-layer-library.md).

**Status:** Merged.

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| `data class` | Immutable value type with copy/equals | Java record / Lombok `@Value` |
| `suspend` / coroutines | Async without blocking threads | `async`/`await` |
| Flow / StateFlow | Values over time | RxJS Observable / BehaviorSubject |
| Retrofit | HTTP from an annotated interface | Feign / typed axios |
| kotlinx.serialization | `@Serializable` JSON | Jackson / Gson |
| Room | `@Entity` + `@Dao` over SQLite | JPA + Spring Data |
| DataStore | Typed key-value prefs | localStorage |
| ViewModel | UI state that survives rotation | Page controller / container state |
| `@Composable` | UI function that rebuilds on state | React function component |
| Coil | `AsyncImage` loader | img + CDN helper |
| `@HiltViewModel` | Constructor-injected ViewModel | `@Autowired` controller |
| `Fake*` in tests | In-memory test double | Jest mock |

## Code to focus on

1. [Song.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/Song.kt) — `require(fileName.isNotBlank())` at the type boundary.
2. [LumenApi.kt](../../core/network/src/main/java/com/anhtuta/lilimusic/core/network/LumenApi.kt) — `suspend` + `@GET` + `@Query`; catalog client. `recordListen` was added in [PR #15](04-playback.md).
3. [NetworkSong.kt](../../core/network/src/main/java/com/anhtuta/lilimusic/core/network/model/NetworkSong.kt) — `@SerialName("file_name")` snake_case → camelCase.
4. [SongEntity.kt](../../core/database/src/main/java/com/anhtuta/lilimusic/core/database/SongEntity.kt) / [SongDao.kt](../../core/database/src/main/java/com/anhtuta/lilimusic/core/database/SongDao.kt) — Room + Flow `observeAll()`.
5. [OfflineFirstSongRepository.kt](../../core/data/src/main/java/com/anhtuta/lilimusic/core/data/repository/OfflineFirstSongRepository.kt) — observe Room; refresh API then `replaceAll`.
6. [LibraryViewModel.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryViewModel.kt) — `combine` → `stateIn` StateFlow (like a React selector). Row tap plays the **full catalog**; search only filters the view ([task 010](08-web-parity.md)).
7. [LibraryScreen.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryScreen.kt) — Scaffold, `PullToRefreshBox`, `LazyColumn`, Coil.
8. [SortPreferencesDataSource.kt](../../core/datastore/src/main/java/com/anhtuta/lilimusic/core/datastore/SortPreferencesDataSource.kt) — DataStore sort keys.

Retrofit surface (catalog GET from this phase; `PUT` listens landed in playback):

```kotlin
@GET("api/song/all")
suspend fun getSongs(
    @Query("sortBy") sortBy: String,
    @Query("sortOrder") sortOrder: String,
): List<NetworkSong>
```

Playable-only at the type boundary, from `Song.kt`:

```kotlin
init {
    require(fileName.isNotBlank()) {
        "Song.fileName must be non-blank; hide Zing-only tracks before creating Song"
    }
}
```

Offline-first, from `OfflineFirstSongRepository.kt`:

```kotlin
override fun observePlayableSongs(): Flow<List<Song>> =
    local.observeSongs().map { entities -> entities.map { it.toSong() } }

override suspend fun refresh(
    sort: SongSort,
    order: SortOrder,
): Result<Unit> =
    withContext(ioDispatcher) {
        runCatching {
            val playable = network.getSongs(sort, order).mapNotNull { it.toPlayableSong(baseUrl) }
            local.replaceAll(playable.map { it.toEntity() })
        }
    }
```

`observePlayableSongs()` is the Room Flow mapped to `Song`. `refresh()` fetches the network, `mapNotNull` `toPlayableSong` (drops empty `file_name`), then `local.replaceAll`. UI never calls Retrofit.
