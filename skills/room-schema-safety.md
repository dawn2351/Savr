# Savr — Room DB Safety (keep your users' bookmarks alive!)

The #1 rule of this app:

> **No matter what you change in the database, your users' bookmarks MUST survive the next release. Blowing away the DB = blowing away people's saved links. Never do it.**

Current DB version is `5` (`data/local/BookmarkDatabase.kt:15`).

---

## The 1 rule that makes everything safe

**Every release that touches the DB = exactly ONE version bump + exactly ONE migration object that covers ALL your changes.**

That's it. You can add 20 tables and 30 columns in a release — just:
1. Write one `Migration(5, 6)` containing all the `CREATE TABLE` / `ALTER TABLE` statements.
2. Change `@Database(version = 5)` → `version = 6`.
3. Register it with `.addMigrations(...)` in `di/SavrModule.kt`.
4. **Delete** `.fallbackToDestructiveMigration(true)` from `SavrModule.kt:29`. If it's ever there, that's a data-wipe bomb.

---

### The two ways to break it (never do these)

| What you might do | What happens |
|-------------------|--------------|
| Change the schema (add table/column/index) but **don't** bump `version` | App crashes on launch for all existing users (`IllegalStateException`). Not a wipe, but a broken app. |
| Bump `version` but add **no** migration | With the destructive fallback still on → **all bookmarks deleted for every user**. This already happened once (v1.3.3, 4→5). Never again. |

---

## Copy‑paste template

Here's the pattern. For example, adding a `colorTag` column to bookmarks:

```kotlin
// data/local/BookmarkDatabase.kt — inside the abstract class
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `bookmarks` ADD COLUMN `colorTag` TEXT NOT NULL DEFAULT ''")
    }
}
```

New columns MUST have a `DEFAULT` (so existing rows don't break). If you don't want to hand-write SQL, Room can
dump the exact schema JSON — copy the `CREATE TABLE` from `app/schemas/.../<version>/<version>.json`.

```kotlin
// di/SavrModule.kt
Room.databaseBuilder(get(), BookmarkDatabase::class.java, "bookmark_db")
    .addMigrations(BookmarkDatabase.MIGRATION_5_6)   // no destructive fallback here!
    .build()
```

---

## A few extra rules (just as important)

- **Never hard-delete bookmarks.** Deleting = set `isHidden = 1` (that's the soft-delete the app already uses in `BookmarkDao.kt`). Re-saving the same URL brings it back automatically.
- **Never overwrite a backup with empty data.** `BackupManager` writes a backup whenever bookmarks change; if the DB is ever empty, it would save an empty backup on top of the good one. Guard it — only save when there are bookmarks.
- **Test before shipping:** install the previous version, save some bookmarks, install the new version on top, confirm the bookmarks are still there. If they're gone, you did it wrong.

---

Have questions? Just remember the golden rule: **one version bump + one migration + no destructive fallback = users' data always safe.**