package com.zarnth.savr.data.update

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

@Serializable
data class GitHubRelease(
    val tag_name: String = "",
    val body: String = "",
    val assets: List<GitHubAsset> = emptyList()
)

@Serializable
data class GitHubAsset(
    val name: String = "",
    val browser_download_url: String = ""
)

data class UpdateInfo(
    val versionName: String,
    val notes: String,
    val apkUrl: String?
)

class UpdateChecker(
    private val context: Context
) {
    companion object {
        private const val REPOSITORY = "qeiq/Savr"
        private const val LATEST_RELEASE_URL = "https://api.github.com/repos/$REPOSITORY/releases/latest"
        private const val PLAY_STORE_INSTALLER = "com.android.vending"
    }

    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    fun isPlayStoreInstall(): Boolean = runCatching {
        context.packageManager.getInstallerPackageName(context.packageName) == PLAY_STORE_INSTALLER
    }.getOrDefault(false)

    fun currentVersionName(): String = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
    }.getOrDefault("")

    fun cacheDir(): File = context.cacheDir

    suspend fun checkForUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(LATEST_RELEASE_URL)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "Savr-Android")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                val release = json.decodeFromString<GitHubRelease>(body)
                val apk = release.assets.firstOrNull { it.name.endsWith(".apk") }
                UpdateInfo(
                    versionName = release.tag_name.removePrefix("v"),
                    notes = release.body,
                    apkUrl = apk?.browser_download_url
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun downloadApk(url: String, destination: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext false
                val stream = response.body?.byteStream() ?: return@withContext false
                destination.outputStream().use { out -> stream.copyTo(out) }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

fun compareVersions(latest: String, current: String): Int {
    fun parts(version: String): List<Int> {
        val cleaned = version.trim().removePrefix("v")
        val nums = cleaned.split(".").mapNotNull { it.toIntOrNull() }
        return nums + List(3 - nums.size) { 0 }
    }

    val l = parts(latest)
    val c = parts(current)
    for (i in 0 until 3) {
        if (l[i] != c[i]) return l[i].compareTo(c[i])
    }
    return 0
}
