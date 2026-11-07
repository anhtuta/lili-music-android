# 062 — Lyric card compose / export / share

## Goal

A listener can open **Create lyric card** from now-playing, pick lyric lines, configure colours / overlay / font like the web `LyricCard` flow, preview a square card, then share a JPEG and copy the selected text.

## Approach

Implements PLAN Phase 6 task **062** only. Backend unchanged. Listener only. Package `com.anhtuta.lilimusic`.

Authoritative web: class `BaiHat.js` `createLyricCard` / `LyricCardModal` + `LyricSelectionModal` + `LyricDownloadModal` + `CardConfiguration` in the attached `liliana-player-reactjs` source.

Web flow:

1. Quote-right control in the lyric playground settings bar.
2. If `isNoLyric()`, toast **No lyric available to create a card**.
3. Selection modal shows `div_result.innerText` (rendered lyric, including translations when translate is on, plus meta footer).
4. User highlights text (desktop) or taps **Confirm selection (mobile)**.
5. Configure: background color vs song image, text align, text colour, bold, font family, font size 5–40, image overlay 0–80%, brightness 0–60%. Defaults and `localStorage` keys `lyricCard_*`.
6. Square preview (CSS `aspect-ratio: 1/1`, padding 40px, lyric `pre-wrap`, title/artist footer with a top border).
7. **Download Card** (`html-to-image` JPEG, `pixelRatio: 4`, filename `lyric-card-{titleNoSpaces}-{artistNoSpaces}.jpg`) and **Copy Text**.

Android port:

- **Entry:** a Card action on the now-playing app bar **and** on the lyrics pane toolbar (web’s control lives on the playground settings overlay; Android settings is a separate screen without lyric text).
- **Guard:** missing lyric (`Lyric doesn't exist!` / empty) shows the same warning string; the card UI does not open.
- **Selection:** tap-to-toggle whole lines (plus Select all). Android has no `window.getSelection()`; this is the mobile equivalent of web’s “Confirm selection (mobile)”. Selected lines join with newlines. Translations follow the current `translate` setting. Empty countdown slots are omitted from selectable text.
- **Config:** same defaults, presets, and ranges as web. Persist in DataStore under `lyricCard_*` keys (web `LILIANA_SETTINGS`).
- **Preview + export:** Compose preview plus `LyricCardRenderer` drawing a square JPEG at 4× the web 450px card (1800px), matching overlay stacking (cover → black overlay → white brightness). Share via `FileProvider` + `ShareCompat` (Android stand-in for a file download). Copy uses the clipboard.
- **Fonts:** web Google fonts (Open Sans, Verdana, Georgia, Lavishly Yours, Lobster, Pacifico, Borel). Android maps those names to system `SANS_SERIF` / `SERIF` / italic sans. Decorative faces are **not** bundled (documented mismatch).

### Modules

- **`:core:model`** — `LyricCardSettings`, `LyricCardFont`, hex helpers, JPEG filename.
- **`:core:lyrics`** — `LyricCardText` (plain lines + meta footer from `ParsedLyric`).
- **`:core:datastore`** — `LyricCardPreferences` on the existing preferences DataStore.
- **`:core:data`** — `LyricCardSettingsRepository`.
- **`:feature:lyrics`** — selection + configuration UI, renderer, ViewModel, share/copy.
- **`:feature:player`** — Card action on the now-playing app bar.
- **`:app`** — FileProvider + cache path; host the card overlay from `LiliApp`.

### Non-goals

App Links (063), Play signing (064), release smoke (065), waveform / `RECORD_AUDIO`, Zing, admin/login, changing hello_lumen, bundling Google Fonts, writing the JPEG into the public Pictures store (share sheet is the download).

## Files

- `docs/tasks/062-lyric-card.md` (this file)
- `core/model/` — settings + filename + tests
- `core/lyrics/` — `LyricCardText` + tests
- `core/datastore/` — preferences + tests
- `core/data/` — repository + fake + tests
- `feature/lyrics/` — UI, renderer, ViewModel, strings, tests
- `feature/player/` — Card action + tests
- `app/` — FileProvider, overlay host, manifest tests

## Tests

- **model (JVM):** defaults match web; font/align parse fallbacks; filename strips whitespace; invalid hex falls back.
- **lyrics engine (JVM):** LRC/XLRC/TRC lines; translations only when requested; meta footer; empty lyric → no card text.
- **datastore (Robolectric + Turbine):** persist/restore every `lyricCard_*` key; missing keys → web defaults (`useSongImage` true, overlay 60, font Open Sans, align left).
- **data (Turbine):** repository forwards setters.
- **renderer (Robolectric):** solid-colour card is square; corner pixels match background; JPEG bytes decode; overlay darkens a white cover; brightness lightens.
- **ViewModel (Turbine):** missing lyric does not open; toggle lines builds selected text; generate moves to configure; share writes JPEG; copy records text.
- **Compose (Robolectric):** Card button; no-lyric warning; select line → generate; config chips; copy/share/back-to-selection callbacks.
- **player Compose:** Card action fires.
- **app:** FileProvider authority and cache-path in the merged manifest.

No emulator. No live backend writes.

## Done when

- Task plan committed **before** code (this file).
- Listener can select lyric lines, configure the card, preview, share a JPEG, and copy text.
- Missing lyric shows the web warning and does not open the card.
- Config persists across process death.
- Tests above exist and `./gradlew check` passes for this slice (full check at PR end).
