package com.wuji.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.wuji.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "wuji_settings")

@Singleton
class DataStoreManager @Inject constructor(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SHOW_HISTORY = booleanPreferencesKey("show_history")
        val SONG_AUTO_SWITCH = booleanPreferencesKey("song_auto_switch")
        val AUTO_UPDATE_SOURCE = booleanPreferencesKey("auto_update_source")
        val DOWNLOAD_PATH = stringPreferencesKey("download_path")
        val READING_THEME = stringPreferencesKey("reading_theme")
        val READING_FONT = stringPreferencesKey("reading_font")
        val READING_FONT_SIZE = intPreferencesKey("reading_font_size")
        val READING_LINE_HEIGHT = floatPreferencesKey("reading_line_height")
        val READING_MODE = stringPreferencesKey("reading_mode")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val SUBSCRIBE_SOURCES = stringPreferencesKey("subscribe_sources")
        val BOOK_SHELVES = stringPreferencesKey("book_shelves")
        val COMIC_SHELVES = stringPreferencesKey("comic_shelves")
        val PHOTO_SHELVES = stringPreferencesKey("photo_shelves")
        val SONG_SHELVES = stringPreferencesKey("song_shelves")
        val VIDEO_SHELVES = stringPreferencesKey("video_shelves")
        val BOOK_HISTORY = stringPreferencesKey("book_history")
        val COMIC_HISTORY = stringPreferencesKey("comic_history")
        val VIDEO_HISTORY = stringPreferencesKey("video_history")
        val USER_INFO = stringPreferencesKey("user_info")
        val CLOUD_SYNC_SETTINGS = stringPreferencesKey("cloud_sync_settings")
        val TTS_VOICE = stringPreferencesKey("tts_voice")
        val TTS_RATE = floatPreferencesKey("tts_rate")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_MODE] ?: false }
    val showHistory: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHOW_HISTORY] ?: true }
    val songAutoSwitch: Flow<Boolean> = context.dataStore.data.map { it[Keys.SONG_AUTO_SWITCH] ?: true }
    val autoUpdateSource: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_UPDATE_SOURCE] ?: true }
    val downloadPath: Flow<String> = context.dataStore.data.map { it[Keys.DOWNLOAD_PATH] ?: "" }
    val readingTheme: Flow<String> = context.dataStore.data.map { it[Keys.READING_THEME] ?: "default" }
    val readingFont: Flow<String> = context.dataStore.data.map { it[Keys.READING_FONT] ?: "default" }
    val readingFontSize: Flow<Int> = context.dataStore.data.map { it[Keys.READING_FONT_SIZE] ?: 18 }
    val readingLineHeight: Flow<Float> = context.dataStore.data.map { it[Keys.READING_LINE_HEIGHT] ?: 1.6f }
    val readingMode: Flow<String> = context.dataStore.data.map { it[Keys.READING_MODE] ?: "scroll" }
    val keepScreenOn: Flow<Boolean> = context.dataStore.data.map { it[Keys.KEEP_SCREEN_ON] ?: false }
    val firstLaunch: Flow<Boolean> = context.dataStore.data.map { it[Keys.FIRST_LAUNCH] ?: true }

    suspend fun setDarkMode(value: Boolean) { context.dataStore.edit { it[Keys.DARK_MODE] = value } }
    suspend fun setShowHistory(value: Boolean) { context.dataStore.edit { it[Keys.SHOW_HISTORY] = value } }
    suspend fun setSongAutoSwitch(value: Boolean) { context.dataStore.edit { it[Keys.SONG_AUTO_SWITCH] = value } }
    suspend fun setAutoUpdateSource(value: Boolean) { context.dataStore.edit { it[Keys.AUTO_UPDATE_SOURCE] = value } }
    suspend fun setDownloadPath(value: String) { context.dataStore.edit { it[Keys.DOWNLOAD_PATH] = value } }
    suspend fun setReadingTheme(value: String) { context.dataStore.edit { it[Keys.READING_THEME] = value } }
    suspend fun setReadingFont(value: String) { context.dataStore.edit { it[Keys.READING_FONT] = value } }
    suspend fun setReadingFontSize(value: Int) { context.dataStore.edit { it[Keys.READING_FONT_SIZE] = value } }
    suspend fun setReadingLineHeight(value: Float) { context.dataStore.edit { it[Keys.READING_LINE_HEIGHT] = value } }
    suspend fun setReadingMode(value: String) { context.dataStore.edit { it[Keys.READING_MODE] = value } }
    suspend fun setKeepScreenOn(value: Boolean) { context.dataStore.edit { it[Keys.KEEP_SCREEN_ON] = value } }
    suspend fun setFirstLaunch(value: Boolean) { context.dataStore.edit { it[Keys.FIRST_LAUNCH] = value } }

    suspend fun getSubscribeSources(): List<SubscribeSource> {
        var result: List<SubscribeSource> = emptyList()
        context.dataStore.data.map { prefs ->
            prefs[Keys.SUBSCRIBE_SOURCES]?.let {
                runCatching { json.decodeFromString<List<SubscribeSource>>(it) }.getOrNull()
            } ?: emptyList()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveSubscribeSources(sources: List<SubscribeSource>) {
        context.dataStore.edit { it[Keys.SUBSCRIBE_SOURCES] = json.encodeToString(sources) }
    }

    suspend fun getBookShelves(): List<BookShelf> {
        var result: List<BookShelf> = emptyList()
        context.dataStore.data.map { prefs ->
            prefs[Keys.BOOK_SHELVES]?.let {
                runCatching { json.decodeFromString<List<BookShelf>>(it) }.getOrNull()
            } ?: emptyList()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveBookShelves(shelves: List<BookShelf>) {
        context.dataStore.edit { it[Keys.BOOK_SHELVES] = json.encodeToString(shelves) }
    }

    suspend fun getComicShelves(): List<ComicShelf> {
        var result: List<ComicShelf> = emptyList()
        context.dataStore.data.map { prefs ->
            prefs[Keys.COMIC_SHELVES]?.let {
                runCatching { json.decodeFromString<List<ComicShelf>>(it) }.getOrNull()
            } ?: emptyList()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveComicShelves(shelves: List<ComicShelf>) {
        context.dataStore.edit { it[Keys.COMIC_SHELVES] = json.encodeToString(shelves) }
    }

    suspend fun getPhotoShelves(): List<PhotoShelf> {
        var result: List<PhotoShelf> = emptyList()
        context.dataStore.data.map { prefs ->
            prefs[Keys.PHOTO_SHELVES]?.let {
                runCatching { json.decodeFromString<List<PhotoShelf>>(it) }.getOrNull()
            } ?: emptyList()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun savePhotoShelves(shelves: List<PhotoShelf>) {
        context.dataStore.edit { it[Keys.PHOTO_SHELVES] = json.encodeToString(shelves) }
    }

    suspend fun getSongShelves(): List<SongShelf> {
        var result: List<SongShelf> = emptyList()
        context.dataStore.data.map { prefs ->
            prefs[Keys.SONG_SHELVES]?.let {
                runCatching { json.decodeFromString<List<SongShelf>>(it) }.getOrNull()
            } ?: emptyList()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveSongShelves(shelves: List<SongShelf>) {
        context.dataStore.edit { it[Keys.SONG_SHELVES] = json.encodeToString(shelves) }
    }

    suspend fun getVideoShelves(): List<VideoShelf> {
        var result: List<VideoShelf> = emptyList()
        context.dataStore.data.map { prefs ->
            prefs[Keys.VIDEO_SHELVES]?.let {
                runCatching { json.decodeFromString<List<VideoShelf>>(it) }.getOrNull()
            } ?: emptyList()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveVideoShelves(shelves: List<VideoShelf>) {
        context.dataStore.edit { it[Keys.VIDEO_SHELVES] = json.encodeToString(shelves) }
    }

    suspend fun getBookHistory(): List<BookHistory> {
        var result: List<BookHistory> = emptyList()
        context.dataStore.data.map { prefs ->
            prefs[Keys.BOOK_HISTORY]?.let {
                runCatching { json.decodeFromString<List<BookHistory>>(it) }.getOrNull()
            } ?: emptyList()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveBookHistory(history: List<BookHistory>) {
        context.dataStore.edit { it[Keys.BOOK_HISTORY] = json.encodeToString(history) }
    }

    suspend fun getComicHistory(): List<ComicHistory> {
        var result: List<ComicHistory> = emptyList()
        context.dataStore.data.map { prefs ->
            prefs[Keys.COMIC_HISTORY]?.let {
                runCatching { json.decodeFromString<List<ComicHistory>>(it) }.getOrNull()
            } ?: emptyList()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveComicHistory(history: List<ComicHistory>) {
        context.dataStore.edit { it[Keys.COMIC_HISTORY] = json.encodeToString(history) }
    }

    suspend fun getVideoHistory(): List<VideoHistory> {
        var result: List<VideoHistory> = emptyList()
        context.dataStore.data.map { prefs ->
            prefs[Keys.VIDEO_HISTORY]?.let {
                runCatching { json.decodeFromString<List<VideoHistory>>(it) }.getOrNull()
            } ?: emptyList()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveVideoHistory(history: List<VideoHistory>) {
        context.dataStore.edit { it[Keys.VIDEO_HISTORY] = json.encodeToString(history) }
    }

    suspend fun getUserInfo(): UserInfo? {
        var result: UserInfo? = null
        context.dataStore.data.map { prefs ->
            prefs[Keys.USER_INFO]?.let {
                runCatching { json.decodeFromString<UserInfo>(it) }.getOrNull()
            }
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveUserInfo(user: UserInfo?) {
        context.dataStore.edit { prefs ->
            if (user != null) {
                prefs[Keys.USER_INFO] = json.encodeToString(user)
            } else {
                prefs.remove(Keys.USER_INFO)
            }
        }
    }

    suspend fun getCloudSyncSettings(): CloudSyncSettings {
        var result = CloudSyncSettings()
        context.dataStore.data.map { prefs ->
            prefs[Keys.CLOUD_SYNC_SETTINGS]?.let {
                runCatching { json.decodeFromString<CloudSyncSettings>(it) }.getOrNull()
            } ?: CloudSyncSettings()
        }.collect { result = it; return@collect }
        return result
    }

    suspend fun saveCloudSyncSettings(settings: CloudSyncSettings) {
        context.dataStore.edit { it[Keys.CLOUD_SYNC_SETTINGS] = json.encodeToString(settings) }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
