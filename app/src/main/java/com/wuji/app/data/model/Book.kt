package com.wuji.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BookItem(
    val id: String = "",
    val sourceId: String = "",
    val title: String = "",
    val author: String = "",
    val intro: String = "",
    val cover: String = "",
    val category: String = "",
    val status: String = "",
    val updateTime: String = "",
    val lastChapter: String = "",
    val url: String = "",
)

@Serializable
data class BookChapter(
    val id: String = "",
    val title: String = "",
    val url: String = "",
    val time: String = "",
)

@Serializable
data class BookList(
    val list: List<BookItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 20,
)

@Serializable
data class BookDetail(
    val item: BookItem = BookItem(),
    val chapters: List<BookChapter> = emptyList(),
)

@Serializable
data class BookContent(
    val chapter: BookChapter = BookChapter(),
    val content: String = "",
    val sourceId: String = "",
)

@Serializable
data class BookShelfItem(
    val item: BookItem = BookItem(),
    val sourceId: String = "",
    val shelfId: String = "",
    val addedAt: Long = 0L,
    val lastReadChapterId: String = "",
    val lastReadChapterTitle: String = "",
    val lastReadTime: Long = 0L,
    val unreadCount: Int = 0,
)

@Serializable
data class BookShelf(
    val id: String = "",
    val name: String = "",
    val items: List<BookShelfItem> = emptyList(),
    val createdAt: Long = 0L,
)

@Serializable
data class BookHistory(
    val item: BookItem = BookItem(),
    val sourceId: String = "",
    val lastReadChapter: BookChapter = BookChapter(),
    val lastReadTime: Long = 0L,
)
