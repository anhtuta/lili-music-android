# Privacy policy — Lili Music

This is the policy text for the Play Store listing of **Lili Music** (`com.anhtuta.lilimusic`). Host it at an HTTPS URL you control and paste that URL into Play Console → App content → Privacy policy. This repository does not change the Lumen backend or the web player.

**Last updated:** 2026-11-07

## Who we are

Lili Music is a listener-only Android client for the existing public catalog at `https://blog.nenmongvietnam.com/laravel/public`. There is no account, no admin UI, and no in-app registration.

Contact: [taanhtu95@gmail.com](mailto:taanhtu95@gmail.com)

## What the app accesses

| Data | Why |
| --- | --- |
| Internet | Load the song list, stream or download audio / lyrics / cover art from the catalog API |
| Notifications | Playback and download foreground-service notifications (Android 13+ asks at runtime) |
| Foreground services | Media playback (`mediaPlayback`) and offline downloads (`dataSync`) |

The app does **not** request microphone access (`RECORD_AUDIO`), location, contacts, or storage of your files outside its private directory.

## What is stored on the device

- Settings (sort, theme, lyric-card options, last song) in app preferences
- A local cache of the catalog, lyrics, and optional offline downloads in app-private storage
- Shared lyric-card JPEGs in the app cache, offered through the system share sheet

Uninstalling the app deletes this data. There is no cloud account to delete.

## What is sent to the catalog

The public Lumen API already used by the web player: song list, audio, lyrics, cover images, and a listen count (`PUT /api/song/listens?path=`) about 60 seconds after a song starts from the beginning. The app does not send a user identifier.

## What we do not collect

No login, no advertising SDK, no analytics/crash SDK, no sale of personal data.

## Children

The catalog is general music. The app is not directed at children and does not knowingly collect personal information from children.

## Changes

Updates to this policy will be posted at the same hosted URL and reflected in a new app version when the Play listing changes.
