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
class ComicRepository @Inject constructor(
    private val sourceRepo: SourceRepository,
    private val dataStore: DataStoreManager,
) {
    private val _shelves = MutableStateFlow<List<ComicShelf>>(emptyList())
    val shelves: StateFlow<List<ComicShelf>> = _shelves
    private val _history = MutableStateFlow<List<ComicHistory>>(emptyList())
    val history: StateFlow<List<ComicHistory>> = _history

    suspend fun loadShelves() { _shelves.value = dataStore.getComicShelves() }
    suspend fun loadHistory() { _history.value = dataStore.getComicHistory() }

    suspend fun searchAll(keyword: String, pageNo: Int): ComicList {
        val sources = sourceRepo.getEnabledSources(SourceType.COMIC)
        var combined = ComicList(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? ComicSourceExtension ?: continue
                val result = ext.search(keyword, pageNo)
                combined = combined.copy(list = combined.list + result.list, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Comic search failed: ${source.id}") }
        }
        return combined
    }

    suspend fun getRecommendAll(pageNo: Int, type: String? = null): ComicList {
        val sources = sourceRepo.getEnabledSources(SourceType.COMIC)
        var combined = ComicList(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? ComicSourceExtension ?: continue
                val result = ext.getRecommendComics(pageNo, type)
                combined = combined.copy(list = combined.list + result.list, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Comic recommend failed: ${source.id}") }
        }
        return combined
    }

    suspend fun getComicDetail(sourceId: String, item: ComicItem): ComicDetail {
        val ext = sourceRepo.getExtension(sourceId, SourceType.COMIC) as? ComicSourceExtension
        return ext?.getComicDetail(item) ?: ComicDetail()
    }

    suspend fun getContent(sourceId: String, item: ComicItem, chapter: ComicChapter): ComicContent {
        val ext = sourceRepo.getExtension(sourceId, SourceType.COMIC) as? ComicSourceExtension
        return ext?.getContent(item, chapter) ?: ComicContent()
    }

    suspend fun createShelf(name: String) {
        val shelf = ComicShelf(id = java.util.UUID.randomUUID().toString(), name = name, createdAt = System.currentTimeMillis())
        _shelves.value = _shelves.value + shelf
        dataStore.saveComicShelves(_shelves.value)
    }

    suspend fun removeShelf(shelfId: String) {
        _shelves.value = _shelves.value.filterNot { it.id == shelfId }
        dataStore.saveComicShelves(_shelves.value)
    }

    suspend fun addToShelf(shelfId: String, item: ComicItem, sourceId: String) {
        _shelves.value = _shelves.value.map { shelf ->
            if (shelf.id == shelfId) {
                shelf.copy(items = shelf.items + ComicShelfItem(item = item, sourceId = sourceId, shelfId = shelfId, addedAt = System.currentTimeMillis()))
            } else shelf
        }
        dataStore.saveComicShelves(_shelves.value)
    }

    suspend fun removeFromShelf(shelfId: String, itemId: String) {
        _shelves.value = _shelves.value.map { shelf ->
            if (shelf.id == shelfId) shelf.copy(items = shelf.items.filterNot { it.item.id == itemId }) else shelf
        }
        dataStore.saveComicShelves(_shelves.value)
    }

    suspend fun updateReadProgress(item: ComicItem, sourceId: String, chapter: ComicChapter) {
        val history = _history.value.toMutableList()
        val idx = history.indexOfFirst { it.item.id == item.id && it.sourceId == sourceId }
        val entry = ComicHistory(item = item, sourceId = sourceId, lastReadChapter = chapter, lastReadTime = System.currentTimeMillis())
        if (idx >= 0) history[idx] = entry else history.add(entry)
        _history.value = history.sortedByDescending { it.lastReadTime }
        dataStore.saveComicHistory(_history.value)
    }

    suspend fun clearHistory() { _history.value = emptyList(); dataStore.saveComicHistory(emptyList()) }
}

@Singleton
class PhotoRepository @Inject constructor(
    private val sourceRepo: SourceRepository,
    private val dataStore: DataStoreManager,
) {
    private val _shelves = MutableStateFlow<List<PhotoShelf>>(emptyList())
    val shelves: StateFlow<List<PhotoShelf>> = _shelves

    suspend fun loadShelves() { _shelves.value = dataStore.getPhotoShelves() }

    suspend fun searchAll(keyword: String, pageNo: Int): PhotoList {
        val sources = sourceRepo.getEnabledSources(SourceType.PHOTO)
        var combined = PhotoList(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? PhotoSourceExtension ?: continue
                val result = ext.search(keyword, pageNo)
                combined = combined.copy(list = combined.list + result.list, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Photo search failed: ${source.id}") }
        }
        return combined
    }

    suspend fun getRecommendAll(pageNo: Int): PhotoList {
        val sources = sourceRepo.getEnabledSources(SourceType.PHOTO)
        var combined = PhotoList(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? PhotoSourceExtension ?: continue
                val result = ext.getRecommendList(pageNo)
                combined = combined.copy(list = combined.list + result.list, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Photo recommend failed: ${source.id}") }
        }
        return combined
    }

    suspend fun getPhotoDetail(sourceId: String, item: PhotoItem, pageNo: Int): PhotoDetail {
        val ext = sourceRepo.getExtension(sourceId, SourceType.PHOTO) as? PhotoSourceExtension
        return ext?.getPhotoDetail(item, pageNo) ?: PhotoDetail()
    }

    suspend fun createShelf(name: String) {
        val shelf = PhotoShelf(id = java.util.UUID.randomUUID().toString(), name = name, createdAt = System.currentTimeMillis())
        _shelves.value = _shelves.value + shelf
        dataStore.savePhotoShelves(_shelves.value)
    }

    suspend fun removeShelf(shelfId: String) {
        _shelves.value = _shelves.value.filterNot { it.id == shelfId }
        dataStore.savePhotoShelves(_shelves.value)
    }

    suspend fun addToShelf(shelfId: String, item: PhotoItem, sourceId: String) {
        _shelves.value = _shelves.value.map { shelf ->
            if (shelf.id == shelfId) {
                shelf.copy(items = shelf.items + PhotoShelfItem(item = item, sourceId = sourceId, shelfId = shelfId, addedAt = System.currentTimeMillis()))
            } else shelf
        }
        dataStore.savePhotoShelves(_shelves.value)
    }

    suspend fun removeFromShelf(shelfId: String, itemId: String) {
        _shelves.value = _shelves.value.map { shelf ->
            if (shelf.id == shelfId) shelf.copy(items = shelf.items.filterNot { it.item.id == itemId }) else shelf
        }
        dataStore.savePhotoShelves(_shelves.value)
    }
}

@Singleton
class SongRepository @Inject constructor(
    private val sourceRepo: SourceRepository,
    private val dataStore: DataStoreManager,
) {
    private val _shelves = MutableStateFlow<List<SongShelf>>(emptyList())
    val shelves: StateFlow<List<SongShelf>> = _shelves

    suspend fun loadShelves() { _shelves.value = dataStore.getSongShelves() }

    suspend fun searchSongsAll(keyword: String, pageNo: Int): SongList {
        val sources = sourceRepo.getEnabledSources(SourceType.SONG)
        var combined = SongList(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? SongSourceExtension ?: continue
                val result = ext.searchSongs(keyword, pageNo)
                combined = combined.copy(list = combined.list + result.list, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Song search failed: ${source.id}") }
        }
        return combined
    }

    suspend fun searchPlaylistsAll(keyword: String, pageNo: Int): PlaylistDetail {
        val sources = sourceRepo.getEnabledSources(SourceType.SONG)
        var combined = PlaylistDetail(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? SongSourceExtension ?: continue
                val result = ext.searchPlaylists(keyword, pageNo)
                combined = combined.copy(songs = combined.songs + result.songs, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Playlist search failed: ${source.id}") }
        }
        return combined
    }

    suspend fun getRecommendPlaylistsAll(pageNo: Int): PlaylistDetail {
        val sources = sourceRepo.getEnabledSources(SourceType.SONG)
        var combined = PlaylistDetail(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? SongSourceExtension ?: continue
                val result = ext.getRecommendPlaylists(pageNo)
                combined = combined.copy(songs = combined.songs + result.songs, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Playlist recommend failed: ${source.id}") }
        }
        return combined
    }

    suspend fun getRecommendSongsAll(pageNo: Int): SongList {
        val sources = sourceRepo.getEnabledSources(SourceType.SONG)
        var combined = SongList(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? SongSourceExtension ?: continue
                val result = ext.getRecommendSongs(pageNo)
                combined = combined.copy(list = combined.list + result.list, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Songs recommend failed: ${source.id}") }
        }
        return combined
    }

    suspend fun getPlaylistDetail(sourceId: String, item: PlaylistInfo, pageNo: Int): PlaylistDetail {
        val ext = sourceRepo.getExtension(sourceId, SourceType.SONG) as? SongSourceExtension
        return ext?.getPlaylistDetail(item, pageNo) ?: PlaylistDetail()
    }

    suspend fun getSongUrl(sourceId: String, item: SongInfo, size: String = "320"): SongUrlMap {
        val ext = sourceRepo.getExtension(sourceId, SourceType.SONG) as? SongSourceExtension
        return ext?.getSongUrl(item, size) ?: SongUrlMap()
    }

    suspend fun getLyric(sourceId: String, item: SongInfo): SongLyric {
        val ext = sourceRepo.getExtension(sourceId, SourceType.SONG) as? SongSourceExtension
        return ext?.getLyric(item) ?: SongLyric()
    }

    suspend fun createShelf(name: String) {
        val shelf = SongShelf(id = java.util.UUID.randomUUID().toString(), name = name, type = SongShelfType.CREATE, createdAt = System.currentTimeMillis())
        _shelves.value = _shelves.value + shelf
        dataStore.saveSongShelves(_shelves.value)
    }

    suspend fun removeShelf(shelfId: String) {
        _shelves.value = _shelves.value.filterNot { it.id == shelfId }
        dataStore.saveSongShelves(_shelves.value)
    }
}

@Singleton
class VideoRepository @Inject constructor(
    private val sourceRepo: SourceRepository,
    private val dataStore: DataStoreManager,
) {
    private val _shelves = MutableStateFlow<List<VideoShelf>>(emptyList())
    val shelves: StateFlow<List<VideoShelf>> = _shelves
    private val _history = MutableStateFlow<List<VideoHistory>>(emptyList())
    val history: StateFlow<List<VideoHistory>> = _history

    suspend fun loadShelves() { _shelves.value = dataStore.getVideoShelves() }
    suspend fun loadHistory() { _history.value = dataStore.getVideoHistory() }

    suspend fun searchAll(keyword: String, pageNo: Int): VideoList {
        val sources = sourceRepo.getEnabledSources(SourceType.VIDEO)
        var combined = VideoList(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? VideoSourceExtension ?: continue
                val result = ext.search(keyword, pageNo)
                combined = combined.copy(list = combined.list + result.list, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Video search failed: ${source.id}") }
        }
        return combined
    }

    suspend fun getRecommendAll(pageNo: Int, type: String? = null): VideoList {
        val sources = sourceRepo.getEnabledSources(SourceType.VIDEO)
        var combined = VideoList(page = pageNo)
        for (source in sources) {
            try {
                val ext = sourceRepo.getExtension(source) as? VideoSourceExtension ?: continue
                val result = ext.getRecommendVideos(pageNo, type)
                combined = combined.copy(list = combined.list + result.list, total = combined.total + result.total)
            } catch (e: Exception) { Timber.e(e, "Video recommend failed: ${source.id}") }
        }
        return combined
    }

    suspend fun getVideoDetail(sourceId: String, item: VideoItem): VideoDetail {
        val ext = sourceRepo.getExtension(sourceId, SourceType.VIDEO) as? VideoSourceExtension
        return ext?.getVideoDetail(item) ?: VideoDetail()
    }

    suspend fun getPlayUrl(sourceId: String, item: VideoItem, resource: VideoResource, episode: VideoEpisode): VideoUrlMap {
        val ext = sourceRepo.getExtension(sourceId, SourceType.VIDEO) as? VideoSourceExtension
        return ext?.getPlayUrl(item, resource, episode) ?: VideoUrlMap()
    }

    suspend fun createShelf(name: String) {
        val shelf = VideoShelf(id = java.util.UUID.randomUUID().toString(), name = name, createdAt = System.currentTimeMillis())
        _shelves.value = _shelves.value + shelf
        dataStore.saveVideoShelves(_shelves.value)
    }

    suspend fun removeShelf(shelfId: String) {
        _shelves.value = _shelves.value.filterNot { it.id == shelfId }
        dataStore.saveVideoShelves(_shelves.value)
    }

    suspend fun addToShelf(shelfId: String, item: VideoItem, sourceId: String) {
        _shelves.value = _shelves.value.map { shelf ->
            if (shelf.id == shelfId) {
                shelf.copy(items = shelf.items + VideoShelfItem(item = item, sourceId = sourceId, shelfId = shelfId, addedAt = System.currentTimeMillis()))
            } else shelf
        }
        dataStore.saveVideoShelves(_shelves.value)
    }

    suspend fun removeFromShelf(shelfId: String, itemId: String) {
        _shelves.value = _shelves.value.map { shelf ->
            if (shelf.id == shelfId) shelf.copy(items = shelf.items.filterNot { it.item.id == itemId }) else shelf
        }
        dataStore.saveVideoShelves(_shelves.value)
    }

    suspend fun updateWatchProgress(item: VideoItem, sourceId: String, episode: VideoEpisode, progress: Long) {
        val history = _history.value.toMutableList()
        val idx = history.indexOfFirst { it.item.id == item.id && it.sourceId == sourceId }
        val entry = VideoHistory(item = item, sourceId = sourceId, lastWatchEpisode = episode, lastWatchTime = System.currentTimeMillis(), watchProgress = progress)
        if (idx >= 0) history[idx] = entry else history.add(entry)
        _history.value = history.sortedByDescending { it.lastWatchTime }
        dataStore.saveVideoHistory(_history.value)
    }

    suspend fun clearHistory() { _history.value = emptyList(); dataStore.saveVideoHistory(emptyList()) }
}
