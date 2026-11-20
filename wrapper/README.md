# Ladybird Expressive Wrapper (prototype)

A from-scratch Android browser shell written in Kotlin + Jetpack Compose.

## UI

- **Material 3 expressive-style** palette, large rounded shapes and tonal surfaces.
- **Vertical tab rail** on the left — one entry per open tab, plus a `+` FAB for new tabs.
- **Arc-style address bar** — a custom curved `Shape` (`ArcShape`) with the URL field and a go FAB inside.
- Web content fills the remaining space; the hardware/system Back button navigates WebView history first.

## Important caveat

This app uses **Android System WebView** as its renderer. It is a UI wrapper/prototype,
**not** the Ladybird browser engine. Swapping in the Ladybird engine requires the upstream
engine to build for Android (see `ANDROID_BUILD_REPORT.md` / the `android-apk.yml` workflow),
which is currently blocked by multiple upstream bugs.

## Build

```sh
cd wrapper
./gradlew assembleRelease
```
