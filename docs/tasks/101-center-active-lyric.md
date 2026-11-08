# 101 — Center the active lyric line

## Goal

Auto-scroll so the active lyric line is vertically centered in the pane (web `scrollLyric`), animated, including rows with pinyin/translation. Do not fight the user while they are dragging the list.

## Approach

Web (`BaiHat.js` `scrollLyric`):

```js
div_result.scrollTop = 20 + parent.offsetTop - firstWord.offsetTop
  - div_result.offsetHeight / 2 + parent.clientHeight / 2;
```

Replace `animateScrollToItem(index)` (item at **top**) with a centered scroll: if the item is visible, `animateScrollBy` so its center meets the viewport center; otherwise scroll to it, then correct. Account for `item.size` (title + optional translation).

Skip the auto-scroll while `LazyListState.interactionSource` reports an in-progress `DragInteraction` (no existing pause today). After the gesture, the next line change recenters.

Non-goals: changing swipe-to-dismiss (094) or the lyric handle (083).

## Files

- `feature/lyrics/.../LyricsPane.kt`
- `feature/lyrics/.../LyricPaneScroll.kt` (pure offset math)
- tests

## Tests

Unit: `centerDelta` is `itemCenter - viewportCenter`. `shouldAutoScroll(userDragging)` is false while dragging. Compose: a tall pane with the active line in the middle places `ACTIVE_LINE` near the pane’s vertical center.

## Done when

- Active line is centered, animated; translations/pinyin height included.
- Manual drag is not overridden mid-gesture.
- Tests pass; `./gradlew check`.
