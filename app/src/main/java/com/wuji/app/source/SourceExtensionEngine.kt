package com.wuji.app.source

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.wuji.app.data.model.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SourceExtensionEngine(private val context: Context) {

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    @Volatile
    private var webView: WebView? = null

    @Volatile
    private var webViewReady = false

    private val initLock = CompletableDeferred<Boolean>()

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun ensureWebView() {
        if (webViewReady) return
        withContext(Dispatchers.Main) {
            if (webViewReady) return@withContext
            webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.blockNetworkImage = true
                settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                webViewClient = WebViewClient()
                addJavascriptInterface(JsBridge(), "WujiNative")
                loadDataWithBaseURL(
                    "about:blank",
                    BASE_HTML,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
            webViewReady = true
            initLock.complete(true)
        }
    }

    suspend fun loadExtension(code: String, sourceId: String, sourceName: String, type: SourceType): SourceExtension? {
        ensureWebView()
        return withContext(Dispatchers.Main) {
            try {
                val js = """
                    (function() {
                        try {
                            $code
                            return 'OK';
                        } catch(e) {
                            return 'ERROR:' + e.message;
                        }
                    })();
                """.trimIndent()
                val result = webView?.evaluateJavascriptSync(js) ?: "ERROR:No WebView"
                if (result.startsWith("ERROR")) {
                    Timber.e("Failed to load extension: $result")
                    null
                } else {
                    createExtensionWrapper(sourceId, sourceName, type)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load extension")
                null
            }
        }
    }

    private fun createExtensionWrapper(sourceId: String, sourceName: String, type: SourceType): SourceExtension {
        return when (type) {
            SourceType.BOOK -> BookExtensionWrapper(sourceId, sourceName, this)
            SourceType.COMIC -> ComicExtensionWrapper(sourceId, sourceName, this)
            SourceType.PHOTO -> PhotoExtensionWrapper(sourceId, sourceName, this)
            SourceType.SONG -> SongExtensionWrapper(sourceId, sourceName, this)
            SourceType.VIDEO -> VideoExtensionWrapper(sourceId, sourceName, this)
        }
    }

    suspend fun callExtension(method: String, vararg args: String): String? {
        ensureWebView()
        return withContext(Dispatchers.Main) {
            val jsArgs = args.joinToString(",") { it }
            val js = """
                (function() {
                    try {
                        if (typeof window.__wujiExt !== 'undefined' && typeof window.__wujiExt.$method === 'function') {
                            return JSON.stringify(window.__wujiExt.$method($jsArgs));
                        }
                        return '{"error":"method not found: $method"}';
                    } catch(e) {
                        return '{"error":"' + e.message + '"}';
                    }
                })();
            """.trimIndent()
            webView?.evaluateJavascriptSync(js)
        }
    }

    fun fetchUrl(url: String, headers: Map<String, String> = emptyMap()): String {
        return try {
            val builder = Request.Builder().url(url).get()
            headers.forEach { (key, value) -> builder.header(key, value) }
            httpClient.newCall(builder.build()).execute().use { response ->
                response.body?.string() ?: ""
            }
        } catch (e: Exception) {
            Timber.e(e, "fetchUrl failed: $url")
            ""
        }
    }

    fun fetchDom(url: String, headers: Map<String, String> = emptyMap()): String {
        return try {
            val builder = Request.Builder().url(url).get()
            headers.forEach { (key, value) -> builder.header(key, value) }
            httpClient.newCall(builder.build()).execute().use { response ->
                val html = response.body?.string() ?: ""
                val doc = Jsoup.parse(html, url)
                doc.html()
            }
        } catch (e: Exception) {
            Timber.e(e, "fetchDom failed: $url")
            ""
        }
    }

    inner class JsBridge {
        @JavascriptInterface
        fun fetch(url: String, headersJson: String?): String {
            val headers = if (headersJson != null) {
                json.decodeFromString<Map<String, String>>(headersJson)
            } else emptyMap()
            return fetchUrl(url, headers)
        }

        @JavascriptInterface
        fun fetchDom(url: String, headersJson: String?): String {
            val headers = if (headersJson != null) {
                json.decodeFromString<Map<String, String>>(headersJson)
            } else emptyMap()
            return this@SourceExtensionEngine.fetchDom(url, headers)
        }
    }

    companion object {
        private const val BASE_HTML = """
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"></head>
<body>
<script>
window.__wujiExt = null;
window.WujiLog = function(msg) { console.log(msg); };
</script>
</body>
</html>
        """
    }
}

private fun WebView.evaluateJavascriptSync(script: String): String? {
    val result = CompletableDeferred<String?>()
    evaluateJavascript(script) { value ->
        result.complete(value)
    }
    return runCatching {
        kotlinx.coroutines.runBlocking { result.await() }
    }.getOrNull()
}
