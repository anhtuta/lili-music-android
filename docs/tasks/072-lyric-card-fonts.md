# 072 — Bundle lyric-card Google Fonts

## Goal

The lyric card font picker uses the same **decorative Google Fonts** as the web (`Lavishly Yours`, `Lobster`, `Pacifico`, `Borel`) instead of italic system-sans fallbacks. Fonts are bundled as `res/font` (OFL; license files included). APK size stays reasonable (regular weight only).

## Approach

Web `CardConfiguration.scss`:

```
@import url('https://fonts.googleapis.com/css2?family=Lavishly+Yours&family=Lobster&family=Pacifico&family=Borel&display=swap');
```

`index.html` also loads Open Sans. Open Sans / Verdana / Georgia stay **system** sans/serif (PLAN §10) so we do not ship extra latin weights.

- Download official TTF Regular files from [google/fonts](https://github.com/google/fonts) (`ofl/lobster`, `ofl/pacifico`, `ofl/lavishlyyours`, `ofl/borel`).
- Place them in `feature/lyrics/src/main/res/font/` with Android names (`lobster_regular.ttf`, …).
- Copy each `OFL.txt` to `feature/lyrics/src/main/res/raw/` (distributed with the APK) and `docs/licenses/` for review.
- `LyricCardFonts` (or equivalent) maps `LyricCardFont` → `ResourcesCompat.getFont` / Compose `FontFamily`.
- `LyricCardRenderer` takes an optional `Typeface` on `LyricCardRenderRequest` so JPEG export uses the bundled face.
- Font chips in `FontRow` render the sample name in that family.
- Non-goals: Open Sans TTF, variable fonts, subsetting tools, changing picker labels.

## Files

- `docs/tasks/072-lyric-card-fonts.md` (this file)
- `feature/lyrics/src/main/res/font/*.ttf`
- `feature/lyrics/src/main/res/raw/ofl_*.txt`
- `docs/licenses/`
- `core/model/.../LyricCardFont.kt` (comment: decorative faces are bundled)
- `feature/lyrics/.../LyricCardFonts.kt` (new)
- `feature/lyrics/.../LyricCardRenderer.kt`, `LyricCardRenderRequest.kt`, `LyricCardViewModel.kt`, `LyricCardScreen.kt`
- tests: renderer + resource presence

## Tests

- Robolectric: `ResourcesCompat.getFont` is non-null for Lobster, Pacifico, Lavishly Yours, Borel.
- Renderer with a bundled typeface is not the italic-sans fallback (`Typeface.create(SANS_SERIF, ITALIC)`).
- Existing lyric-card screen/ViewModel tests still pass.
- A small test lists the four OFL raw resources.

## Done when

- Task plan committed before code.
- Picker and export use bundled decorative fonts.
- Licenses are in the tree and the APK (`res/raw`).
- Tests pass.
