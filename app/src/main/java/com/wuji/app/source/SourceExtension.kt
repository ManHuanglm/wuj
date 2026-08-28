package com.wuji.app.source

import com.wuji.app.data.model.*

interface SourceExtension {
    val sourceId: String
    val sourceName: String
    val sourceType: SourceType
}

interface BookSourceExtension : SourceExtension {
    override val sourceType: SourceType get() = SourceType.BOOK
    suspend fun getRecommendBooks(pageNo: Int, type: String?): BookList
    suspend fun search(keyword: String, pageNo: Int): BookList
    suspend fun getBookDetail(item: BookItem): BookDetail
    suspend fun getContent(item: BookItem, chapter: BookChapter): BookContent
}

interface ComicSourceExtension : SourceExtension {
    override val sourceType: SourceType get() = SourceType.COMIC
    suspend fun getRecommendComics(pageNo: Int, type: String?): ComicList
    suspend fun search(keyword: String, pageNo: Int): ComicList
    suspend fun getComicDetail(item: ComicItem): ComicDetail
    suspend fun getContent(item: ComicItem, chapter: ComicChapter): ComicContent
}

interface PhotoSourceExtension : SourceExtension {
    override val sourceType: SourceType get() = SourceType.PHOTO
    suspend fun getRecommendList(pageNo: Int): PhotoList
    suspend fun search(keyword: String, pageNo: Int): PhotoList
    suspend fun getPhotoDetail(item: PhotoItem, pageNo: Int): PhotoDetail
}

interface SongSourceExtension : SourceExtension {
    override val sourceType: SourceType get() = SourceType.SONG
    suspend fun getRecommendPlaylists(pageNo: Int): PlaylistDetail
    suspend fun getRecommendSongs(pageNo: Int): SongList
    suspend fun searchPlaylists(keyword: String, pageNo: Int): PlaylistDetail
    suspend fun searchSongs(keyword: String, pageNo: Int): SongList
    suspend fun getPlaylistDetail(item: PlaylistInfo, pageNo: Int): PlaylistDetail
    suspend fun getSongUrl(item: SongInfo, size: String): SongUrlMap
    suspend fun getLyric(item: SongInfo): SongLyric
}

interface VideoSourceExtension : SourceExtension {
    override val sourceType: SourceType get() = SourceType.VIDEO
    suspend fun getRecommendVideos(pageNo: Int, type: String?): VideoList
    suspend fun search(keyword: String, pageNo: Int): VideoList
    suspend fun getVideoDetail(item: VideoItem): VideoDetail
    suspend fun getPlayUrl(item: VideoItem, resource: VideoResource, episode: VideoEpisode): VideoUrlMap
}
