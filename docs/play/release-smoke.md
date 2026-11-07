# Release smoke (task 065)

CI runs the **automated** half (`make smoke`): minified release APK, R8 mapping, `aapt` package / permission / launcher checks. That job has no emulator.

This page is the **manual** half for the owner, on an **arm64 macOS** emulator (or a physical device). Use Make; Android Studio is optional.

Unsigned release APKs **cannot** be installed. Use the debug APK (`make apk` / `make install`) for this checklist. After you have a keystore ([signing.md](signing.md)), you may additionally install `make release-apk`.

## 1. Emulator

JDK 21+ and the SDK must already pass `make doctor` ([how to run](../learning/05-how-to-run.md)).

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools:$PATH"

sdkmanager "emulator" "system-images;android-35;google_apis;arm64-v8a"
avdmanager create avd -n lili-arm64 -k "system-images;android-35;google_apis;arm64-v8a" -d pixel_7 --force
emulator -avd lili-arm64
```

On Apple Silicon the AVD ABI must be **arm64-v8a**. Wait until the home screen is idle.

## 2. Install

```bash
make doctor
make apk          # dist/lili-music-<version>-debug.apk
make install      # adb install -r
```

Optional automated packaging gate (no emulator):

```bash
make smoke        # minified :app:assembleRelease + aapt
```

## 3. Checklist

Work through these on the running AVD. Tick only what you actually did.

### Library and playable-only

- [ ] Cold start shows the catalog (live API). Pull-to-refresh still works.
- [ ] Songs **without** a `file_name` (Zing-only) are absent. Search does not surface them.
- [ ] Sort chips match the web columns; extra asc/desc toggle still talks to the API.

### Playback and notification

- [ ] Tap a song → now-playing + mini-player. Audio starts.
- [ ] Sequence / shuffle (current song stays first) / repeat-one.
- [ ] Speed ±0.1× between 0.5× and 2.0×.
- [ ] Shade the notification: play, pause, next, previous, seek ±5s. Lock screen if the AVD shows one.
- [ ] After ~60s from position 0 on the same `path`, listen count is allowed to fire (no crash if the network is slow).

### Lyrics

- [ ] A `.lrc` song highlights and autoscrolls.
- [ ] An `.xlrc` song shows translations only when **Translate** is on (default off).
- [ ] A `.trc` song karaoke-fills and shows countdown dots on empty words.
- [ ] Missing lyric shows the contribution message (mail / Facebook), not a crash.
- [ ] ±100ms offset moves the highlight.

### Offline

- [ ] Download a song (audio + lyric + cover). Downloads screen lists it.
- [ ] Enable airplane mode (or disable emulator network). The downloaded song still plays; others do not.
- [ ] Restore network.

### Lyric card

- [ ] Card action on now-playing. Empty lyric warns and does not open the editor.
- [ ] Select lines → configure → preview → **Copy text** and **Share** (share sheet; JPEG).

### Deep link

```bash
adb shell am start -a android.intent.action.VIEW -d "lilimusic://bai-hat/<a-real-path>"
```

- [ ] The player opens that song if it is playable. A nonsense path does not crash.
- [ ] HTTPS App Links are **not** expected to auto-verify until [assetlinks.json](app-links.md) is hosted.

### Settings and chrome

- [ ] Light / dark app theme. Lyric theme dark / light / picture-bg. Gigantic line defaults on.
- [ ] No waveform UI. The app never asks for the microphone.

### Play listing (once, not every build)

- [ ] Host [privacy-policy.md](privacy-policy.md) at an HTTPS URL and paste it into Play Console.
- [ ] Host [assetlinks.json](assetlinks.json) as in [app-links.md](app-links.md).
- [ ] First Play upload is `make bundle` with the upload keystore ([signing.md](signing.md)).
