package com.wuji.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ComicItem(
    val id: String = "",
    val sourceId: String = "",
    val title: String = "",
    val author: String = "",
    val intro: String = "",
    val cover: String = "",
    val category: String = "",
    val status: String = "",
    val updateTime: String = "",
    val url: String = "",
)

@Serializable
data class ComicChapter(
    val id: String = "",
    val title: String = "",
    val url: String = "",
    val time: String = "",
)

@Serializable
data class ComicList(
    val list: List<ComicItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 20,
)

@Serializable
data class ComicDetail(
    val item: ComicItem = ComicItem(),
    val chapters: List<ComicChapter> = emptyList(),
)

@Serializable
data class ComicContent(
    val chapter: ComicChapter = ComicChapter(),
    val images: List<String> = emptyList(),
    val sourceId: String = "",
)

@Serializable
data class ComicShelfItem(
    val item: ComicItem = ComicItem(),
    val sourceId: String = "",
    val shelfId: String = "",
    val addedAt: Long = 0L,
    val lastReadChapterId: String = "",
    val lastReadChapterTitle: String = "",
    val lastReadTime: Long = 0L,
    val unreadCount: Int = 0,
)

@Serializable
data class ComicShelf(
    val id: String = "",
    val name: String = "",
    val items: List<ComicShelfItem> = emptyList(),
    val createdAt: Long = 0L,
)

@Serializable
data class ComicHistory(
    val item: ComicItem = ComicItem(),
    val sourceId: String = "",
    val lastReadChapter: ComicChapter = ComicChapter(),
    val lastReadTime: Long = 0L,
)
