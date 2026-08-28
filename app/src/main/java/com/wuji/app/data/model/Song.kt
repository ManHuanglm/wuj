package com.wuji.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class SongPlayMode { SINGLE, LIST, RANDOM }

@Serializable
enum class SongShelfType { CREATE, LIKE, PLAYLIST }

@Serializable
data class SongInfo(
    val id: String = "",
    val sourceId: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val cover: String = "",
    val duration: Long = 0L,
    val url: String = "",
)

@Serializable
data class SongList(
    val list: List<SongInfo> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 20,
)

@Serializable
data class PlaylistInfo(
    val id: String = "",
    val sourceId: String = "",
    val title: String = "",
    val cover: String = "",
    val author: String = "",
    val description: String = "",
    val songCount: Int = 0,
    val url: String = "",
)

@Serializable
data class PlaylistDetail(
    val info: PlaylistInfo = PlaylistInfo(),
    val songs: List<SongInfo> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
)

@Serializable
data class SongUrlMap(
    val url: String = "",
    val size: String = "",
    val type: String = "",
)

@Serializable
data class SongLyric(
    val lyric: String = "",
    val translatedLyric: String = "",
)

@Serializable
data class SongShelfItem(
    val song: SongInfo = SongInfo(),
    val sourceId: String = "",
    val shelfId: String = "",
    val addedAt: Long = 0L,
)

@Serializable
data class SongShelf(
    val id: String = "",
    val name: String = "",
    val type: SongShelfType = SongShelfType.CREATE,
    val items: List<SongShelfItem> = emptyList(),
    val createdAt: Long = 0L,
)

@Serializable
data class SongPlaylistShelf(
    val id: String = "",
    val info: PlaylistInfo = PlaylistInfo(),
    val sourceId: String = "",
    val addedAt: Long = 0L,
)
