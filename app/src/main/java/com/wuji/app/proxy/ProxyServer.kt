package com.wuji.app.proxy

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProxyServer @Inject constructor() {

    private val serverRef = AtomicReference<NettyApplicationEngine?>(null)
    private var port = 1430

    private val httpClient by lazy {
        HttpClient(OkHttp) {
            engine { config { followRedirects(true) } }
        }
    }

    suspend fun start(): Int {
        serverRef.get()?.let { return port }
        for (attemptPort in 1430..1530) {
            try {
                val server = embeddedServer(Netty, host = "127.0.0.1", port = attemptPort) {
                    routing {
                        get("/proxy/{headers}/{url}") { handleProxy(call) }
                        get("/m3u8/{headers}/{url}") { handleM3u8(call) }
                        get("/ts/{headers}/{url}") { handleTs(call) }
                    }
                }
                server.start(wait = false)
                serverRef.set(server)
                port = attemptPort
                Timber.i("Proxy server started on port $port")
                return port
            } catch (e: Exception) {
                Timber.w("Port $attemptPort unavailable: ${e.message}")
            }
        }
        throw RuntimeException("Failed to start proxy server")
    }

    fun stop() {
        serverRef.getAndSet(null)?.stop(1000, 2000)
    }

    fun getProxyUrl(url: String, headers: Map<String, String> = emptyMap()): String {
        val headersJson = kotlinx.serialization.json.Json.encodeToString(headers)
        val encodedHeaders = java.net.URLEncoder.encode(headersJson, "UTF-8")
        val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
        return "http://127.0.0.1:$port/proxy/$encodedHeaders/$encodedUrl"
    }

    fun getM3u8Url(url: String, headers: Map<String, String> = emptyMap()): String {
        val headersJson = kotlinx.serialization.json.Json.encodeToString(headers)
        val encodedHeaders = java.net.URLEncoder.encode(headersJson, "UTF-8")
        val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
        return "http://127.0.0.1:$port/m3u8/$encodedHeaders/$encodedUrl"
    }

    private suspend fun handleProxy(call: ApplicationCall) {
        val url = URLDecoder.decode(call.parameters["url"] ?: "", "UTF-8")
        val headersJson = URLDecoder.decode(call.parameters["headers"] ?: "{}", "UTF-8")
        val headers = runCatching {
            kotlinx.serialization.json.Json.decodeFromString<Map<String, String>>(headersJson)
        }.getOrDefault(emptyMap())
        try {
            val response = httpClient.get(url) { headers.forEach { (k, v) -> header(k, v) } }
            val contentType = response.contentType()
            call.respondOutputStream(
                if (contentType != null) ContentType.parse(contentType.toString()) else ContentType.Application.OctetStream
            ) { writeBytes(response.readBytes()) }
        } catch (e: Exception) {
            Timber.e(e, "Proxy failed: $url")
            call.respond(HttpStatusCode.InternalServerError, "Proxy error: ${e.message}")
        }
    }

    private suspend fun handleM3u8(call: ApplicationCall) {
        val url = URLDecoder.decode(call.parameters["url"] ?: "", "UTF-8")
        val headersJson = URLDecoder.decode(call.parameters["headers"] ?: "{}", "UTF-8")
        val headers = runCatching {
            kotlinx.serialization.json.Json.decodeFromString<Map<String, String>>(headersJson)
        }.getOrDefault(emptyMap())
        try {
            val response = httpClient.get(url) { headers.forEach { (k, v) -> header(k, v) } }
            val content = response.bodyAsText()
            val baseUrl = url.substringBeforeLast("/") + "/"
            val rewritten = content.lines().joinToString("\n") { line ->
                line.trim().let { l ->
                    when {
                        l.startsWith("#") -> l
                        l.isBlank() -> l
                        l.startsWith("http") -> getProxyUrl(l, headers)
                        else -> getProxyUrl(baseUrl + l, headers)
                    }
                }
            }
            call.respondText(rewritten, ContentType.parse("application/vnd.apple.mpegurl"))
        } catch (e: Exception) {
            Timber.e(e, "M3U8 proxy failed: $url")
            call.respond(HttpStatusCode.InternalServerError, "M3U8 proxy error")
        }
    }

    private suspend fun handleTs(call: ApplicationCall) {
        handleProxy(call)
    }
}
