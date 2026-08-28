package com.wuji.app.data.repository

import com.wuji.app.data.local.DataStoreManager
import com.wuji.app.data.model.*
import com.wuji.app.source.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceRepository @Inject constructor(
    private val dataStore: DataStoreManager,
    private val engine: SourceExtensionEngine,
) {
    private val _subscribeSources = MutableStateFlow<List<SubscribeSource>>(emptyList())
    val subscribeSources: StateFlow<List<SubscribeSource>> = _subscribeSources

    private val extensionCache = mutableMapOf<String, SourceExtension>()

    suspend fun loadSources() {
        val sources = dataStore.getSubscribeSources()
        _subscribeSources.value = sources
    }

    suspend fun addSubscribeSource(url: String): Boolean {
        return try {
            val response = okhttp3.OkHttpClient().newCall(
                okhttp3.Request.Builder().url(url).build()
            ).execute()
            val content = response.body?.string() ?: return false
            val source = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }.decodeFromString<SubscribeSource>(content)
            val updated = _subscribeSources.value + source.copy(url = url, lastUpdate = System.currentTimeMillis())
            _subscribeSources.value = updated
            dataStore.saveSubscribeSources(updated)
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to add subscribe source: $url")
            false
        }
    }

    suspend fun removeSubscribeSource(id: String) {
        val updated = _subscribeSources.value.filterNot { it._id == id }
        _subscribeSources.value = updated
        dataStore.saveSubscribeSources(updated)
        extensionCache.keys.filter { it.startsWith(id) }.forEach { extensionCache.remove(it) }
    }

    suspend fun updateSubscribeSource(id: String): Boolean {
        val source = _subscribeSources.value.find { it._id == id } ?: return false
        return try {
            val response = okhttp3.OkHttpClient().newCall(
                okhttp3.Request.Builder().url(source.url).build()
            ).execute()
            val content = response.body?.string() ?: return false
            val updated = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }.decodeFromString<SubscribeSource>(content)
            val list = _subscribeSources.value.map {
                if (it._id == id) updated.copy(url = source.url, lastUpdate = System.currentTimeMillis())
                else it
            }
            _subscribeSources.value = list
            dataStore.saveSubscribeSources(list)
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to update subscribe source: $id")
            false
        }
    }

    suspend fun enableSource(sourceId: String, itemId: String, enabled: Boolean) {
        val list = _subscribeSources.value.map { source ->
            if (source._id == sourceId) {
                val urls = source.detail.urls.map { item ->
                    if (item.id == itemId) item.copy(disable = !enabled) else item
                }
                source.copy(detail = source.detail.copy(urls = urls))
            } else source
        }
        _subscribeSources.value = list
        dataStore.saveSubscribeSources(list)
    }

    fun getEnabledSources(type: SourceType): List<SubscribeItem> {
        return _subscribeSources.value.flatMap { source ->
            source.detail.urls.filter { !it.disable && it.type == type }
        }
    }

    suspend fun getExtension(item: SubscribeItem): SourceExtension? {
        val cacheKey = "${item.id}"
        extensionCache[cacheKey]?.let { return it }

        return try {
            val ext = engine.loadExtension(item.code, item.id, item.name, item.type)
            if (ext != null) {
                extensionCache[cacheKey] = ext
            }
            ext
        } catch (e: Exception) {
            Timber.e(e, "Failed to load extension: ${item.id}")
            null
        }
    }

    suspend fun getExtension(sourceId: String, type: SourceType): SourceExtension? {
        val item = getEnabledSources(type).find { it.id == sourceId } ?: return null
        return getExtension(item)
    }
}
