# Ladybird Android (arm64) — auto-build & auto-release

This repository automatically builds the **arm64-v8a Android APK of Ladybird**
from the **original repository** ([LadybirdBrowser/ladybird](https://github.com/LadybirdBrowser/ladybird))
and publishes every build to the [**Releases**](https://github.com/niggetron/Ladybirldglm/releases) tab.

## How it works

The workflow [`.github/workflows/android-apk.yml`](.github/workflows/android-apk.yml):

1. Checks out the latest `master` of the original Ladybird repo.
2. Installs the Android SDK/NDK (NDK r29 beta, SDK 35), JDK 17, Rust, wasm-tools and vcpkg (pinned by Ladybird's `vcpkg.json`).
3. Patches the app so it builds **arm64-v8a only** and stamps a fresh version name.
4. Runs `./gradlew assembleRelease` in `UI/Android`, signs the APK, and
5. publishes a new GitHub Release tagged `nightly-<date>-<commit>` with version name `1.0-nightly-<date>-<commit>`.

## Triggers

| Trigger | Behavior |
|---|---|
| **Every push** to this repo | Auto-build + auto-publish a release |
| **Every 7 days** (cron, Mon 00:00 UTC) | Auto-build latest upstream master + auto-publish |
| **Manual** | "Run workflow" button on the Actions tab |

## Requirements to install

- Android 11+ device with an arm64 CPU (virtually all modern phones).

> Ladybird's Android app is a work-in-progress port of the browser — builds may occasionally fail
> if upstream code is mid-refactor. Each successful build appears in Releases with its build date
> and upstream commit.
