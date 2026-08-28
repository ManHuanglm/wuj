package com.wuji.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class SyncType { PHOTO_SHELF, SONG_SHELF, BOOK_SHELF, COMIC_SHELF, VIDEO_SHELF, SUBSCRIBE_SOURCE }

@Serializable
data class SyncOption(
    val type: SyncType,
    val enabled: Boolean = true,
    val isIncremental: Boolean = true,
)

@Serializable
data class SyncTombstone(
    val type: String = "",
    val entityId: String = "",
    val parentId: String = "",
    val deletedAt: Long = 0L,
)

@Serializable
data class SyncUploadItem(
    val type: String = "",
    val data: String = "",
    val tombstones: List<SyncTombstone> = emptyList(),
)

@Serializable
data class SyncDownloadRecord(
    val type: String = "",
    val data: String = "",
    val deletedEntities: List<SyncTombstone> = emptyList(),
    val updatedAt: Long = 0L,
)

@Serializable
data class CloudSyncSettings(
    val enabled: Boolean = false,
    val syncTypes: List<SyncOption> = emptyList(),
    val lastSyncTime: Long = 0L,
    val autoSyncInterval: Long = 3600000L,
)

@Serializable
data class Announcement(
    val _id: String = "",
    val audience: String = "login",
    val title: String = "",
    val content: String = "",
    val link: String = "",
    val emoji: String = "",
    val variant: String = "default",
    val enabled: Boolean = true,
    val sortOrder: Int = 0,
    val startAt: Long = 0L,
    val endAt: Long = 0L,
)
