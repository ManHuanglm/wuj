package com.wuji.app.data.repository

import com.wuji.app.data.local.DataStoreManager
import com.wuji.app.data.model.*
import com.wuji.app.source.BookSourceExtension
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val sourceRepo: SourceRepository,
    private val dataStore: DataStoreManager,
) {
    private val _shelves = MutableStateFlow<List<BookShelf>>(emptyList())
    val shelves: StateFlow<List<BookShelf>> = _shelves

    private val _history = MutableStateFlow<List<BookHistory>>(emptyList())
    val history: StateFlow<List<BookHistory>> = _history

    suspend fun loadShelves() { _shelves.value = dataStore.getBookShelves() }
    suspend fun loadHistory() { _history.value = dataStore.getBookHistory() }

    suspend fun getRecommendBooks(sourceId: String, pageNo: Int, type: String? = null): BookList {
        val ext = sourceRepo.getExtension(sourceId, SourceType.BOOK) as? BookSourceExtension
        return ext?.getRecommendBooks(pageNo, type) ?: BookList()
    }

    suspend fun search(sourceId: String, keyword: String, pageNo: Int): BookList {
        val ext = sourceRepo.getExtension(sourceId, SourceType.BOOK) as? BookSourceExtension
        return ext?.search(keyword, pageNo) ?: BookList()
    }

    suspend fun searchAll(keyword: String, pageNo: Int): BookList {
        val sources = sourceRepo.getEnabledSources(SourceType.BOOK)
        var combined = BookList(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? BookSourceExtension ?: continue
                val result = ext.search(keyword, pageNo)
                combined = combined.copy(list = combined.list + result.list, total = combined.total + result.total)
            } catch (e: Exception) {
                Timber.e(e, "Search failed for source: ${source.id}")
            }
        }
        return combined
    }

    suspend fun getBookDetail(sourceId: String, item: BookItem): BookDetail {
        val ext = sourceRepo.getExtension(sourceId, SourceType.BOOK) as? BookSourceExtension
        return ext?.getBookDetail(item) ?: BookDetail()
    }

    suspend fun getContent(sourceId: String, item: BookItem, chapter: BookChapter): BookContent {
        val ext = sourceRepo.getExtension(sourceId, SourceType.BOOK) as? BookSourceExtension
        return ext?.getContent(item, chapter) ?: BookContent()
    }

    suspend fun createShelf(name: String) {
        val shelf = BookShelf(id = java.util.UUID.randomUUID().toString(), name = name, createdAt = System.currentTimeMillis())
        _shelves.value = _shelves.value + shelf
        dataStore.saveBookShelves(_shelves.value)
    }

    suspend fun removeShelf(shelfId: String) {
        _shelves.value = _shelves.value.filterNot { it.id == shelfId }
        dataStore.saveBookShelves(_shelves.value)
    }

    suspend fun addToShelf(shelfId: String, item: BookItem, sourceId: String) {
        _shelves.value = _shelves.value.map { shelf ->
            if (shelf.id == shelfId) {
                val shelfItem = BookShelfItem(item = item, sourceId = sourceId, shelfId = shelfId, addedAt = System.currentTimeMillis())
                shelf.copy(items = shelf.items + shelfItem)
            } else shelf
        }
        dataStore.saveBookShelves(_shelves.value)
    }

    suspend fun removeFromShelf(shelfId: String, itemId: String) {
        _shelves.value = _shelves.value.map { shelf ->
            if (shelf.id == shelfId) {
                shelf.copy(items = shelf.items.filterNot { it.item.id == itemId })
            } else shelf
        }
        dataStore.saveBookShelves(_shelves.value)
    }

    suspend fun updateReadProgress(item: BookItem, sourceId: String, chapter: BookChapter) {
        val history = _history.value.toMutableList()
        val idx = history.indexOfFirst { it.item.id == item.id && it.sourceId == sourceId }
        val entry = BookHistory(item = item, sourceId = sourceId, lastReadChapter = chapter, lastReadTime = System.currentTimeMillis())
        if (idx >= 0) history[idx] = entry else history.add(entry)
        _history.value = history.sortedByDescending { it.lastReadTime }
        dataStore.saveBookHistory(_history.value)
    }

    suspend fun clearHistory() {
        _history.value = emptyList()
        dataStore.saveBookHistory(emptyList())
    }
}
