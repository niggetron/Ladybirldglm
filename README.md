# Ladybird Android (arm64) — auto-build & auto-release

This repository automatically builds **both** the **arm64-v8a (64-bit)** and
**armeabi-v7a (32-bit ARMv7)** Android APKs of Ladybird from the **original repository**
([LadybirdBrowser/ladybird](https://github.com/LadybirdBrowser/ladybird)) and publishes
both APKs in one release on the [**Releases**](https://github.com/niggetron/Ladybirldglm/releases) tab.
The Android app sources are included under [`UI/Android`](UI/Android).

## How it works

The workflow [`.github/workflows/android-apk.yml`](.github/workflows/android-apk.yml):

1. Checks out the latest `master` of the original Ladybird repo.
2. Installs the Android SDK/NDK (NDK r29 beta, SDK 35), JDK 17, Rust, wasm-tools and vcpkg (pinned by Ladybird's `vcpkg.json`).
3. Patches the app with a fresh version name and builds **arm64-v8a** and **armeabi-v7a** separately.
4. Signs both APKs and
5. publishes a new GitHub Release tagged `nightly-<date>-<commit>` with version name `1.0-nightly-<date>-<commit>`, containing both APKs.

## Triggers

| Trigger | Behavior |
|---|---|
| **Every push** to this repo | Auto-build + auto-publish a release |
| **Every 7 days** (cron, Mon 00:00 UTC) | Auto-build latest upstream master + auto-publish |
| **Manual** | "Run workflow" button on the Actions tab |

## Requirements to install

- Android 11+ device with an arm64 CPU (virtually all modern phones) — use the `arm64` APK.
- Older 32-bit ARM devices — use the `armv7` APK.

> Ladybird's Android app is a work-in-progress port of the browser — builds may occasionally fail
> if upstream code is mid-refactor. Each successful build appears in Releases with its build date
> and upstream commit.
