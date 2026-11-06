# 010 — Web player parity fixes

## Goal

Match the class-component web player (`BaiHat.js` + `Player.js` + `BaiHatTable.js` from `anhtuta/liliana-player-reactjs`) on the items the owner chose after a parity review of PR #18 (`cursor/phase-5-player-visuals-cca4`). Android extras stay as documented deviations.

## Approach

Stacked on task 009 / PR #18. Backend unchanged. Listener only. Authoritative web source is the class `BaiHat.js` routed from `App.js` `/bai-hat` — not `BaiHatFunctional.js`.

Each numbered item is implemented in its own Conventional Commit (tests in the same commit as the behaviour). PLAN.md / learning-guide updates are a separate docs commit after the code.

### Change (match web unless noted)

| Item | Decision | Web source | Android target |
| --- | --- | --- | --- |
| **1** | Previous always goes to the previous track. Drop the 3s restart-current rule. `REPEAT_ONE` replays the same song. | `Player.js` `prevSong` | `Media3PlayerController.skipToPrevious`, `RepeatAwarePlayer` (notification / lock screen / headset) |
| **2** | Schedule the 60s listen **only** when playback starts at **exactly** position 0. Cancel **only** when song/`path` changes. Pause, resume, and finishing must **not** cancel. | `BaiHat.js` `onPlay` ~556–575 | `ListenCountPolicy` + scheduler tests |
| **3** | Tapping a song always queues the **full playable catalog** in current sort order. Search only filters the list view. | `UtilService.filterTable`, `Player.js` `playList` | `LibraryViewModel.onSongClick` |
| **5** | Rewind / forward 5s on the player screen. Map Media3 seek back/forward increments to 5000ms so notification and headset can use them. | `BaiHat.js` ArrowLeft / ArrowRight | Player transport + `ExoPlayerFactory` |
| **6** | Previous on the first track is a no-op (keep position) and shows a short message equivalent to the web toast `"This is the first song of playing list!"`. | `Player.js` 135–137 | `PlaybackState` + player UI |
| **8** | Unknown duration (`C.TIME_UNSET`) falls back to **600s** until a real duration arrives. | `Player.js` `getDuration` 206–208 | `PlaybackState.durationMs` |
| **9** | Do **not** re-sort client-side when unfiltered; trust API order for the chosen `sortBy`/`sortOrder` (web re-fetches per column). If any local sort remains, nulls match MySQL (ASC nulls first, DESC nulls last). Keep the extra asc/desc toggle (**item 11**) working via API `sortOrder`. | `BaiHat.js` `getAllSongs`, `BaiHatTable.js` column clicks | `SongCatalog`, Room catalog order, `LibraryViewModel` refresh-on-sort |
| **10** | List rows show listens, Song-of-the-Year badge, type, and formatted created date (`formatCreatedDate` from the API, same field `BaiHatTable.js` renders). Tap cover opens a full-size preview when `image_url` exists. | `BaiHatTable.js` | `LibraryScreen` |
| **14** | Gigantic line default **ON** (`PlayerSettings.giganticLine = true`), matching web first run (`div-res-big-active-line` on the lyric panel). | `BaiHat.js` lyric container class; `Setting.js` only toggles when a stored boolean exists | `PlayerSettings` + PLAN/learning docs that mentioned the old default |
| **15** | Lyric meta footer under synced lines: title – artist, Lyric creator, Source, Date (`vi-VN` m/yyyy). | `BaiHat.js` `appendLyricMeta` ~485–520 | Lyrics pane |
| **16** | Missing-lyric message: email (`mailto:taanhtu95@gmail.com`) and Facebook (`https://fb.com/anhtuta95`) are tappable links. | `Constants.js` `NO_LYRIC` | Lyrics missing state |
| **17** | Countdown dots only when a following word exists. | `BaiHat.js` `countdownAtEmptyWord` ~588–596 | `LyricSynchronizer` |
| **18** | Lyric position ticker **30ms** (was 50ms). | `BaiHat.js` `setPlayLyricInterval` | `Media3PlayerController.POSITION_TICK_MS` |
| **19** | Show album under artist on now-playing: album or `"No Album"`. | `Player.js` 300, `LeftPlayground.js` 21 | `PlayerScreen` |

### Keep as-is (Android extras / choices — document as intentional deviations)

| Item | Decision | Why it stays |
| --- | --- | --- |
| **4** | Speed clamp 0.5–2.0× | Media3 / in-app control; web has no equivalent clamp this tight |
| **7** | Restore last song paused on cold start | Android session restore; web does not |
| **11** | Extra asc/desc toggle | Android extra; item 9 wires it through API `sortOrder` instead of a local re-sort |
| **12** | Instant search (no debounce) | Android extra vs web search box |
| **13** | Tap lyric line to seek | Android extra; web lyric taps do not seek |
| **20** | Cover Palette tint on now-playing | Android extra (web `getAverageRGB` only tinted waveform) |
| **21** | Last-song persistence | Same as 7; DataStore `lastSongPath` |
| **22** | External deep links (`/bai-hat` intent-filters / App Links) | Deferred to the **release phase** (PLAN task **063**). In-app `bai-hat/{path}` navigation already exists. |

Non-goals: waveform / `RECORD_AUDIO`, Zing streaming, admin/login, lyric-card export, downloads, changing hello_lumen.

## Files

Best-effort; stay inside existing modules.

- `docs/tasks/010-web-parity-fixes.md` (this file)
- `docs/PLAN.md`, `docs/learning/README.md`, new `docs/learning/08-web-parity.md`
- `core/playback`: `ListenCountPolicy`, `Media3PlayerController`, `RepeatAwarePlayer`, `ExoPlayerFactory`, `PlayerController`, `FakePlayerController`, `PlaybackState`
- `core/model`: `PlayQueue`, `SongCatalog`, `PlayerSettings`
- `core/database`: catalog insertion order (`catalogIndex` + `ORDER BY`)
- `core/lyrics`: `LyricSynchronizer`, lyric meta date formatting
- `feature/library`: ViewModel queue vs search, list row UI, cover preview
- `feature/player`: ±5s, first-song message, album line
- `feature/lyrics`: meta footer, tappable missing-lyric links
- Matching unit / Compose tests in each module

## Tests

- Previous skip: no 3s restart; `REPEAT_ONE` replays; first-track no-op keeps position (`PlayQueue` / `FakePlayerController` / `SkipPreviousPolicy`)
- `ListenCountPolicyTest`: schedule only at position 0; pause/resume/finish do not cancel; path change cancels
- Library: search filters view; `onSongClick` plays the full catalog; sort/order change re-fetches; unfiltered list keeps API/Room order; MySQL nulls if local comparator is used
- Player Compose: rewind/forward fire; first-song message; album / No Album; seek increments constant 5000
- Duration fallback 600_000ms
- Gigantic line default true
- Lyric meta footer + `vi-VN` month/year; missing lyric Mail/FB links clickable
- Countdown: last empty word (no following word) stays at 0 dots
- Ticker constant 30ms
- Cover preview dialog opens only when `image_url` exists
- Update tests that encoded the old 3s, pause-cancels-listen, client re-sort, gigantic-off, 50ms tick behaviours

## Done when

- [ ] This plan is committed **before** code
- [ ] Every change item has tests; old tests updated
- [ ] Intentional deviations (4, 7, 11–13, 20–22) are written in PLAN.md / the learning guide
- [ ] PLAN.md lists external App Links in the release phase (063) and gigantic-line default is ON
- [ ] `./gradlew check` passes
- [ ] PR is opened against `cursor/phase-5-player-visuals-cca4` (depends on #18; retarget to `main` after #18 merges)
