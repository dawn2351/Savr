package com.zarnth.savr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.zarnth.savr.domain.model.Bookmark
import com.zarnth.savr.domain.repository.BookmarkRepository
import com.zarnth.savr.domain.repository.SettingsRepository
import org.koin.core.context.GlobalContext

class QuickSaveActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)?.trim()
        val quickSaveEnabled = GlobalContext.get().get<SettingsRepository>().getQuickSaveEnabled()

        if (sharedText.isNullOrBlank()) {
            finish()
            return
        }

        if (!quickSaveEnabled) {
            val forward = Intent(this, MainActivity::class.java).apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sharedText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(forward)
            finish()
            return
        }

        val url = extractUrl(sharedText)
        if (url == null) {
            Toast.makeText(this, "No valid link found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val repository = GlobalContext.get().get<BookmarkRepository>()
        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            val inserted = withContext(Dispatchers.IO) {
                repository.insertToHome(Bookmark(url = url, title = null, description = null, imageUrl = null))
            }
            Toast.makeText(
                this@QuickSaveActivity,
                if (inserted) "Saved to Savr" else "Already in Savr",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun extractUrl(text: String): String? {
        val schemeMatch = Regex("""(https?://[^\s<>"']+)""", RegexOption.IGNORE_CASE).find(text)
        val raw = schemeMatch?.value ?: text.trim()
        if (raw.isBlank()) return null
        val cleaned = raw.trim().trimEnd('.', ',', ';', '!', '?', ')', ']', '}')
        if (cleaned.startsWith("http://") || cleaned.startsWith("https://")) return cleaned
        if (cleaned.contains(".") && !cleaned.contains(" ")) return "https://$cleaned"
        return null
    }
}
