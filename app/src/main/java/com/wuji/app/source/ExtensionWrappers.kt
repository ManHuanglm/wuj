package com.wuji.app.source

import com.wuji.app.data.model.*
import kotlinx.serialization.json.Json
import timber.log.Timber

abstract class BaseExtensionWrapper(
    override val sourceId: String,
    override val sourceName: String,
    private val engine: SourceExtensionEngine,
) : SourceExtension {
    protected val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    protected suspend fun callMethod(method: String, vararg args: String): JsonObject? {
        val result = engine.callExtension(method, *args)
        if (result == null || result == "null") return null
        return try {
            val cleaned = result.removeSurrounding("\"").replace("\\\"", "\"").replace("\\\\", "\\")
            val parsed = json.parseToJsonElement(cleaned)
            parsed.jsonObject
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse method result: $method -> $result")
            null
        }
    }
}

class BookExtensionWrapper(
    sourceId: String,
    sourceName: String,
    engine: SourceExtensionEngine,
) : BaseExtensionWrapper(sourceId, sourceName, engine), BookSourceExtension {

    override suspend fun getRecommendBooks(pageNo: Int, type: String?): BookList {
        val res = callMethod("getRecommendBooks", pageNo.toString(), type ?: "")
        return res?.let { json.decodeFromJsonElement(it) } ?: BookList()
    }

    override suspend fun search(keyword: String, pageNo: Int): BookList {
        val res = callMethod("search", json.encodeToString(keyword), pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: BookList()
    }

    override suspend fun getBookDetail(item: BookItem): BookDetail {
        val res = callMethod("getBookDetail", json.encodeToString(item))
        return res?.let { json.decodeFromJsonElement(it) } ?: BookDetail()
    }

    override suspend fun getContent(item: BookItem, chapter: BookChapter): BookContent {
        val res = callMethod("getContent", json.encodeToString(item), json.encodeToString(chapter))
        return res?.let { json.decodeFromJsonElement(it) } ?: BookContent()
    }
}

class ComicExtensionWrapper(
    sourceId: String,
    sourceName: String,
    engine: SourceExtensionEngine,
) : BaseExtensionWrapper(sourceId, sourceName, engine), ComicSourceExtension {

    override suspend fun getRecommendComics(pageNo: Int, type: String?): ComicList {
        val res = callMethod("getRecommendComics", pageNo.toString(), type ?: "")
        return res?.let { json.decodeFromJsonElement(it) } ?: ComicList()
    }

    override suspend fun search(keyword: String, pageNo: Int): ComicList {
        val res = callMethod("search", json.encodeToString(keyword), pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: ComicList()
    }

    override suspend fun getComicDetail(item: ComicItem): ComicDetail {
        val res = callMethod("getComicDetail", json.encodeToString(item))
        return res?.let { json.decodeFromJsonElement(it) } ?: ComicDetail()
    }

    override suspend fun getContent(item: ComicItem, chapter: ComicChapter): ComicContent {
        val res = callMethod("getContent", json.encodeToString(item), json.encodeToString(chapter))
        return res?.let { json.decodeFromJsonElement(it) } ?: ComicContent()
    }
}

class PhotoExtensionWrapper(
    sourceId: String,
    sourceName: String,
    engine: SourceExtensionEngine,
) : BaseExtensionWrapper(sourceId, sourceName, engine), PhotoSourceExtension {

    override suspend fun getRecommendList(pageNo: Int): PhotoList {
        val res = callMethod("getRecommendList", pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: PhotoList()
    }

    override suspend fun search(keyword: String, pageNo: Int): PhotoList {
        val res = callMethod("search", json.encodeToString(keyword), pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: PhotoList()
    }

    override suspend fun getPhotoDetail(item: PhotoItem, pageNo: Int): PhotoDetail {
        val res = callMethod("getPhotoDetail", json.encodeToString(item), pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: PhotoDetail()
    }
}

class SongExtensionWrapper(
    sourceId: String,
    sourceName: String,
    engine: SourceExtensionEngine,
) : BaseExtensionWrapper(sourceId, sourceName, engine), SongSourceExtension {

    override suspend fun getRecommendPlaylists(pageNo: Int): PlaylistDetail {
        val res = callMethod("getRecommendPlaylists", pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: PlaylistDetail()
    }

    override suspend fun getRecommendSongs(pageNo: Int): SongList {
        val res = callMethod("getRecommendSongs", pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: SongList()
    }

    override suspend fun searchPlaylists(keyword: String, pageNo: Int): PlaylistDetail {
        val res = callMethod("searchPlaylists", json.encodeToString(keyword), pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: PlaylistDetail()
    }

    override suspend fun searchSongs(keyword: String, pageNo: Int): SongList {
        val res = callMethod("searchSongs", json.encodeToString(keyword), pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: SongList()
    }

    override suspend fun getPlaylistDetail(item: PlaylistInfo, pageNo: Int): PlaylistDetail {
        val res = callMethod("getPlaylistDetail", json.encodeToString(item), pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: PlaylistDetail()
    }

    override suspend fun getSongUrl(item: SongInfo, size: String): SongUrlMap {
        val res = callMethod("getSongUrl", json.encodeToString(item), json.encodeToString(size))
        return res?.let { json.decodeFromJsonElement(it) } ?: SongUrlMap()
    }

    override suspend fun getLyric(item: SongInfo): SongLyric {
        val res = callMethod("getLyric", json.encodeToString(item))
        return res?.let { json.decodeFromJsonElement(it) } ?: SongLyric()
    }
}

class VideoExtensionWrapper(
    sourceId: String,
    sourceName: String,
    engine: SourceExtensionEngine,
) : BaseExtensionWrapper(sourceId, sourceName, engine), VideoSourceExtension {

    override suspend fun getRecommendVideos(pageNo: Int, type: String?): VideoList {
        val res = callMethod("getRecommendVideos", pageNo.toString(), type ?: "")
        return res?.let { json.decodeFromJsonElement(it) } ?: VideoList()
    }

    override suspend fun search(keyword: String, pageNo: Int): VideoList {
        val res = callMethod("search", json.encodeToString(keyword), pageNo.toString())
        return res?.let { json.decodeFromJsonElement(it) } ?: VideoList()
    }

    override suspend fun getVideoDetail(item: VideoItem): VideoDetail {
        val res = callMethod("getVideoDetail", json.encodeToString(item))
        return res?.let { json.decodeFromJsonElement(it) } ?: VideoDetail()
    }

    override suspend fun getPlayUrl(item: VideoItem, resource: VideoResource, episode: VideoEpisode): VideoUrlMap {
        val res = callMethod("getPlayUrl", json.encodeToString(item), json.encodeToString(resource), json.encodeToString(episode))
        return res?.let { json.decodeFromJsonElement(it) } ?: VideoUrlMap()
    }
}
