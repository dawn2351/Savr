# Savr v1.3.7

## What's new

- **Pin important bookmarks.** Pin a bookmark on Home (or inside a collection) to keep it at the top — pinned items are preserved in your backups too.
- **Rename collections.** Long-press the pencil on a collection card to rename it.
- **Add links straight into a collection.** Tap the **+** button inside any collection and paste a link — it's saved into that collection only, without showing up on your Home screen.
- **Smart clipboard popup.** When you're inside a collection, the "add this link?" popup now saves into that collection (and the button says so). On Home it still saves as a normal bookmark.

## Bugs fixed

- **Collection screen no longer goes blank.** Switching tabs and coming back to a collection used to show it empty — the bookmarks now stay loaded.
- **Instant duplicate detection.** Adding a link that already exists in the same collection is caught right away with a clear message, instead of only being checked later.
- **Empty links blocked.** Trying to save an empty URL from inside a collection is now ignored cleanly.

## Performance

- **Faster collection queries.** Added database indexes on the collection↔bookmark links, so opening a collection and listing its bookmarks is noticeably snappier.
- **No data loss.** All upgrades migrate your existing bookmarks, pins, and collection-only links safely to the new storage format.

## Files

- `app-release.apk` — signed installable APK (GitHub direct downloads)
- `app-release.aab` — signed App Bundle (for the Play Store)
