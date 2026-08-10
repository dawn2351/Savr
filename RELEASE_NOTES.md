# Savr v1.3.3

## What's new

### Crashes & stability

- **Crash logs** — if the app ever stops unexpectedly, the full details are saved privately on your device. Open **Settings → About → Crash logs** to review the crash report — app version, Android version, device model, thread, and the complete stack trace — and copy it to share with the developer.
- **100% private** — crash reports never leave your device. Savr still has no analytics.

### Updates

- **Automatic update check** — Savr now checks for a newer version every time it opens and shows the update sheet automatically when one is available. No need to open Settings; if you're up to date, nothing pops up.
- **Readable changelogs** — release notes are now rendered as formatted Markdown in the update sheet.
- **Update sheet polish** — the notes area scrolls independently so the **Download & install** and **Not now** buttons are always visible, even with long changelogs.

## What's fixed

- Update sheet buttons could be pushed off-screen when the changelog was very long; the changelog now scrolls in its own area while the buttons stay pinned.
- Launched a safer foundation: Kotlin 2.3.10, KSP 2.3.10, and compileSdk 37.

## Files

- `app-release.apk` — signed installable APK (GitHub direct downloads)
- `app-release.aab` — signed App Bundle (for the Play Store)