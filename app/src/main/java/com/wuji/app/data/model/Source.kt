package com.wuji.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class SourceType { PHOTO, SONG, VIDEO, BOOK, COMIC }

@Serializable
data class Source(
    val id: String = "",
    val name: String = "",
    val type: SourceType = SourceType.BOOK,
    val url: String = "",
    val disabled: Boolean = false,
    val code: String = "",
)

@Serializable
data class SubscribeItem(
    val id: String = "",
    val name: String = "",
    val type: SourceType = SourceType.BOOK,
    val url: String = "",
    val disable: Boolean = false,
    val code: String = "",
)

@Serializable
data class SubscribeDetail(
    val version: String = "1",
    val urls: List<SubscribeItem> = emptyList(),
)

@Serializable
data class SubscribeSource(
    val _id: String = "",
    val url: String = "",
    val name: String = "",
    val detail: SubscribeDetail = SubscribeDetail(),
    val disable: Boolean = false,
    val isLocal: Boolean = false,
    val lastUpdate: Long = 0L,
)

@Serializable
data class MarketSource(
    val _id: String = "",
    val name: String = "",
    val author: String = "",
    val version: String = "1",
    val permissions: List<String> = emptyList(),
    val sourceContents: List<MarketSourceContent> = emptyList(),
    val isPublic: Boolean = true,
    val isBanned: Boolean = false,
    val thumbsUp: Int = 0,
    val liked: Boolean = false,
    val imported: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)

@Serializable
data class MarketSourceContent(
    val _id: String = "",
    val name: String = "",
    val type: SourceType = SourceType.BOOK,
    val disabled: Boolean = false,
    val source: String = "",
    val url: String = "",
    val code: String = "",
)

@Serializable
data class PagedMarketSource(
    val list: List<MarketSource> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 20,
)
