package com.wuji.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class DownloadCategory { VIDEO, MUSIC, BOOK, COMIC, PHOTO }

@Serializable
enum class DownloadStatus { PENDING, DOWNLOADING, PAUSED, COMPLETED, ERROR }

@Serializable
data class DownloadTask(
    val id: String = "",
    val sourceId: String = "",
    val title: String = "",
    val url: String = "",
    val savePath: String = "",
    val category: DownloadCategory = DownloadCategory.VIDEO,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val totalSize: Long = 0L,
    val downloadedSize: Long = 0L,
    val totalChunks: Int = 0,
    val completedChunks: Int = 0,
    val headers: Map<String, String> = emptyMap(),
    val extra: Map<String, String> = emptyMap(),
    val error: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
) {
    val progress: Float
        get() = if (totalSize > 0) downloadedSize.toFloat() / totalSize else 0f
}

@Serializable
data class DownloadChunk(
    val index: Int,
    val url: String,
    val savePath: String,
    val completed: Boolean = false,
    val size: Long = 0L,
)
