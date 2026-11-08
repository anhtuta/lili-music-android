# 075 — Smooth `.trc` karaoke fill

## Goal

Word-level karaoke fill on `.trc` (and anything currently parsed as TRC) animates smoothly, like `.lrc` and like web `BaiHat.js` `runKaraokeEffect`. Root cause is investigated and tested, not guessed.

## Approach

Web (`BaiHat.js` class + `BaiHatService.runKaraokeEffect`): `setInterval(updateLyric, 30)` while playing. Fill is `((currentTime - st) * 100) / (en - st)` written as a CSS `linear-gradient` on the **current** `<span>` only (no React re-render). `HTMLAudioElement.currentTime` is a continuous clock. `.lrc` is one span per line (seconds long). `.trc` is one span per word (often 150–400ms).

**Root cause on Android (confirmed before code):**

1. **Coarse position (primary).** `Media3PlayerController` copies `Player.currentPosition` every 30ms. ExoPlayer’s ms clock often holds or jumps, unlike web `currentTime`. A 3s LRC line still looks smooth at ~1% per tick; a 200ms TRC word jumps ~15%+ and can skip whole words of fill if the player reports the same position for 100–200ms.
2. **Recompose (secondary).** `LyricsViewModel` runs `LyricSynchronizer.sync` on every position emission, putting `fill` on every `LyricWordFrame`. `KaraokeLine` rebuilds an `AnnotatedString` + `SpanStyle.brush` for all words on that line. TRC lines have many spans, so ticks drop; LRC has one span.
3. **066 brush is not the jump.** `Brush.horizontalGradient` already uses a **fractional** `word.fill` hard-stop (same as CSS `process%` then rest at `0%`). Jumps are `fill` stepping, not the brush snapping to 0/1.

Fix:

- `PlaybackPositionInterpolator`: `positionMs + elapsedRealtimeDelta * speed` between player samples (paused / missing stamp → sampled position). Fake-clock unit tests.
- Stamp `positionSampledAtElapsedMs` on `PlaybackState` only when `currentPosition` (or play/pause/seek) changes.
- Lyrics UI: `withFrameNanos` interpolates display position; **draw-phase** fill (`drawWithContent` + `SrcAtop` clip of the active word boxes) so the `AnnotatedString` is `remember`ed without `fill` in the key.
- Keep the 30ms ExoPlayer poll (web interval). Do not change listen-count or seek semantics beyond using interpolated display time for karaoke.

**`.xtrc`:** Web comment is “Will support `.xtrc` in the future”. The `else` branch still builds TRC spans with `hasTranslation = false`. Android `LyricFormat.fromFileName("song.xtrc")` is already `TRC` (no translations). **Do not add `.xtrc` translation support.** Record the mismatch in PLAN §10.

Non-goals: changing LRC parse, waveform, new lyric formats, 16ms ExoPlayer polling as the only fix.

## Files

- `docs/tasks/075-smooth-trc-karaoke.md` (this file)
- `core/lyrics/.../PlaybackPositionInterpolator.kt` + tests
- `core/lyrics/.../LyricSynchronizer.kt` (`startMs` / `endMs` on `LyricWordFrame`; karaokeFill already fractional)
- `core/playback/.../PlaybackState.kt`, `Media3PlayerController.kt` (sample stamp)
- `feature/lyrics/.../LyricsUiState.kt`, `LyricsViewModel.kt`, `LyricsPane.kt` (draw fill + vsync interpolate)
- tests: interpolator fake clock; karaokeFill 0.37; existing pane wrap tests still pass

## Tests

- Interpolator: paused unchanged; 1.0× 100ms → +100; 1.5× 100ms → +150; stamp 0 / not playing → sampled position; elapsed going backward does not decrease position.
- `karaokeFill` at 370ms of a 1000ms word is `0.37` (fractional, not 0/1).
- Existing TRC wrap / highlight tests still pass.

## Done when

- Task plan committed before code.
- `.trc` fill advances between ExoPlayer samples; AnnotatedString for a line is stable while only fill changes.
- `.xtrc` is documented, not newly implemented.
- Tests pass.
