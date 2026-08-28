package com.wuji.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoItem(
    val id: String = "",
    val sourceId: String = "",
    val title: String = "",
    val cover: String = "",
    val intro: String = "",
    val category: String = "",
    val year: String = "",
    val area: String = "",
    val url: String = "",
)

@Serializable
data class VideoEpisode(
    val id: String = "",
    val title: String = "",
    val url: String = "",
)

@Serializable
data class VideoResource(
    val name: String = "",
    val episodes: List<VideoEpisode> = emptyList(),
)

@Serializable
data class VideoList(
    val list: List<VideoItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 20,
)

@Serializable
data class VideoDetail(
    val item: VideoItem = VideoItem(),
    val resources: List<VideoResource> = emptyList(),
)

@Serializable
data class VideoUrlMap(
    val url: String = "",
    val type: String = "",
    val headers: Map<String, String> = emptyMap(),
)

@Serializable
data class VideoShelfItem(
    val item: VideoItem = VideoItem(),
    val sourceId: String = "",
    val shelfId: String = "",
    val addedAt: Long = 0L,
    val lastWatchEpisodeId: String = "",
    val lastWatchEpisodeTitle: String = "",
    val lastWatchTime: Long = 0L,
    val watchProgress: Long = 0L,
)

@Serializable
data class VideoShelf(
    val id: String = "",
    val name: String = "",
    val items: List<VideoShelfItem> = emptyList(),
    val createdAt: Long = 0L,
)

@Serializable
data class VideoHistory(
    val item: VideoItem = VideoItem(),
    val sourceId: String = "",
    val lastWatchEpisode: VideoEpisode = VideoEpisode(),
    val lastWatchTime: Long = 0L,
    val watchProgress: Long = 0L,
)
