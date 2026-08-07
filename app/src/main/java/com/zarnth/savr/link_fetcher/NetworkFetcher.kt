package com.zarnth.savr.link_fetcher

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit

class NetworkFetcher {

    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    fun fetchDocument(url: String, agent: String): Document? {
        return try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", agent)
                .header("Accept-Language", "en-US,en;q=0.9")
                .build()

            client.newCall(request).execute().use { response ->
                val finalUrl = response.request.url.toString()
                val body = response.body?.string()

                if (!body.isNullOrEmpty()) {
                    Jsoup.parse(body, finalUrl)
                } else null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
