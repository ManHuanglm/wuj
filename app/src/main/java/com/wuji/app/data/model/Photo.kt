package com.wuji.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PhotoItem(
    val id: String = "",
    val sourceId: String = "",
    val title: String = "",
    val cover: String = "",
    val url: String = "",
    val tags: List<String> = emptyList(),
)

@Serializable
data class PhotoList(
    val list: List<PhotoItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val pageSize: Int = 20,
)

@Serializable
data class PhotoDetail(
    val item: PhotoItem = PhotoItem(),
    val images: List<String> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
)

@Serializable
data class PhotoShelfItem(
    val item: PhotoItem = PhotoItem(),
    val sourceId: String = "",
    val shelfId: String = "",
    val addedAt: Long = 0L,
)

@Serializable
data class PhotoShelf(
    val id: String = "",
    val name: String = "",
    val items: List<PhotoShelfItem> = emptyList(),
    val createdAt: Long = 0L,
)
