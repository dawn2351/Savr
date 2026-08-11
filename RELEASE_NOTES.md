# Savr v1.3.4

## What's new

### A small change to Savr's update system

- **Removed the GitHub-based in-app update feature.**

The main reason is that the APK self-update mechanism requires the `REQUEST_INSTALL_PACKAGES` permission, which is subject to Google Play's restricted permission policies and isn't appropriate for Savr's Play Store distribution.

Rather than maintaining separate builds and adding unnecessary complexity just to support the GitHub updater, I've decided to keep Savr's codebase simple and reliable.

For GitHub users, updates will now need to be downloaded manually from the latest GitHub Release.

The Google Play version will continue to receive updates normally through Google Play.

Sorry for the inconvenience, and thank you for understanding. I'd rather remove a feature than keep something around that could create Play Store compliance issues or make Savr harder to maintain.

## What's fixed

- Removed the automatic update check on app startup, the update sheet/UI, and the APK download & install logic.

## Files

- `app-release.apk` — signed installable APK (GitHub direct downloads)
- `app-release.aab` — signed App Bundle (for the Play Store)
