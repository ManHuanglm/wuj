package com.wuji.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wuji.app.data.model.*
import com.wuji.app.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepo: SongRepository,
    private val sourceRepo: SourceRepository,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    private val _songsState = MutableStateFlow<UiState<SongList>>(UiState())
    val songsState: StateFlow<UiState<SongList>> = _songsState
    private val _playlistsState = MutableStateFlow<UiState<PlaylistDetail>>(UiState())
    val playlistsState: StateFlow<UiState<PlaylistDetail>> = _playlistsState
    val shelves: StateFlow<List<SongShelf>> = songRepo.shelves

    init { loadRecommend() }

    fun updateQuery(q: String) { _searchQuery.value = q }
    fun clearSearch() { _searchQuery.value = ""; _songsState.value = UiState(); _playlistsState.value = UiState() }

    fun loadRecommend() {
        viewModelScope.launch {
            _playlistsState.value = UiState.loading()
            try { sourceRepo.loadSources(); _playlistsState.value = UiState.success(songRepo.getRecommendPlaylistsAll(1)) }
            catch (e: Exception) { _playlistsState.value = UiState.error(e.message ?: "????") }
        }
    }

    fun search() {
        val keyword = _searchQuery.value.ifBlank { return }
        viewModelScope.launch {
            _songsState.value = UiState.loading()
            _playlistsState.value = UiState.loading()
            try {
                _songsState.value = UiState.success(songRepo.searchSongsAll(keyword, 1))
                _playlistsState.value = UiState.success(songRepo.searchPlaylistsAll(keyword, 1))
            } catch (e: Exception) {
                _songsState.value = UiState.error(e.message ?: "????")
            }
        }
    }

    fun loadShelves() { viewModelScope.launch { songRepo.loadShelves() } }
    fun createShelf(name: String) { viewModelScope.launch { songRepo.createShelf(name) } }
    fun removeShelf(id: String) { viewModelScope.launch { songRepo.removeShelf(id) } }
    fun getSongUrl(sourceId: String, item: SongInfo, onResult: (SongUrlMap) -> Unit) {
        viewModelScope.launch { onResult(songRepo.getSongUrl(sourceId, item)) }
    }
    fun getLyric(sourceId: String, item: SongInfo, onResult: (SongLyric) -> Unit) {
        viewModelScope.launch { onResult(songRepo.getLyric(sourceId, item)) }
    }
    fun getPlaylistDetail(sourceId: String, item: PlaylistInfo, page: Int, onResult: (PlaylistDetail) -> Unit) {
        viewModelScope.launch { onResult(songRepo.getPlaylistDetail(sourceId, item, page)) }
    }
}

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepo: BookRepository,
    private val sourceRepo: SourceRepository,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    private val _recommendState = MutableStateFlow<UiState<BookList>>(UiState())
    val recommendState: StateFlow<UiState<BookList>> = _recommendState
    private val _searchState = MutableStateFlow<UiState<BookList>>(UiState())
    val searchState: StateFlow<UiState<BookList>> = _searchState
    val shelves: StateFlow<List<BookShelf>> = bookRepo.shelves
    val history: StateFlow<List<BookHistory>> = bookRepo.history

    init { loadRecommend() }

    fun updateQuery(q: String) { _searchQuery.value = q }
    fun clearSearch() { _searchQuery.value = ""; _searchState.value = UiState() }

    fun loadRecommend() {
        viewModelScope.launch {
            _recommendState.value = UiState.loading()
            try { sourceRepo.loadSources(); _recommendState.value = UiState.success(BookList()) }
            catch (e: Exception) { _recommendState.value = UiState.error(e.message ?: "????") }
        }
    }

    fun search() {
        val keyword = _searchQuery.value.ifBlank { return }
        viewModelScope.launch {
            _searchState.value = UiState.loading()
            try { _searchState.value = UiState.success(bookRepo.searchAll(keyword, 1)) }
            catch (e: Exception) { _searchState.value = UiState.error(e.message ?: "????") }
        }
    }

    fun loadShelves() { viewModelScope.launch { bookRepo.loadShelves() } }
    fun loadHistory() { viewModelScope.launch { bookRepo.loadHistory() } }
    fun createShelf(name: String) { viewModelScope.launch { bookRepo.createShelf(name) } }
    fun removeShelf(id: String) { viewModelScope.launch { bookRepo.removeShelf(id) } }
    fun addToShelf(shelfId: String, item: BookItem, sourceId: String) { viewModelScope.launch { bookRepo.addToShelf(shelfId, item, sourceId) } }
    fun removeFromShelf(shelfId: String, itemId: String) { viewModelScope.launch { bookRepo.removeFromShelf(shelfId, itemId) } }
    fun updateReadProgress(item: BookItem, sourceId: String, chapter: BookChapter) { viewModelScope.launch { bookRepo.updateReadProgress(item, sourceId, chapter) } }
    fun clearHistory() { viewModelScope.launch { bookRepo.clearHistory() } }

    fun getBookDetail(sourceId: String, item: BookItem, onResult: (BookDetail) -> Unit) {
        viewModelScope.launch { onResult(bookRepo.getBookDetail(sourceId, item)) }
    }
    fun getContent(sourceId: String, item: BookItem, chapter: BookChapter, onResult: (BookContent) -> Unit) {
        viewModelScope.launch { onResult(bookRepo.getContent(sourceId, item, chapter)) }
    }
}

@HiltViewModel
class ComicViewModel @Inject constructor(
    private val comicRepo: ComicRepository,
    private val sourceRepo: SourceRepository,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    private val _recommendState = MutableStateFlow<UiState<ComicList>>(UiState())
    val recommendState: StateFlow<UiState<ComicList>> = _recommendState
    private val _searchState = MutableStateFlow<UiState<ComicList>>(UiState())
    val searchState: StateFlow<UiState<ComicList>> = _searchState
    val shelves: StateFlow<List<ComicShelf>> = comicRepo.shelves
    val history: StateFlow<List<ComicHistory>> = comicRepo.history

    init { loadRecommend() }

    fun updateQuery(q: String) { _searchQuery.value = q }
    fun clearSearch() { _searchQuery.value = ""; _searchState.value = UiState() }

    fun loadRecommend() {
        viewModelScope.launch {
            _recommendState.value = UiState.loading()
            try { sourceRepo.loadSources(); _recommendState.value = UiState.success(ComicList()) }
            catch (e: Exception) { _recommendState.value = UiState.error(e.message ?: "????") }
        }
    }

    fun search() {
        val keyword = _searchQuery.value.ifBlank { return }
        viewModelScope.launch {
            _searchState.value = UiState.loading()
            try { _searchState.value = UiState.success(comicRepo.searchAll(keyword, 1)) }
            catch (e: Exception) { _searchState.value = UiState.error(e.message ?: "????") }
        }
    }

    fun loadShelves() { viewModelScope.launch { comicRepo.loadShelves() } }
    fun loadHistory() { viewModelScope.launch { comicRepo.loadHistory() } }
    fun createShelf(name: String) { viewModelScope.launch { comicRepo.createShelf(name) } }
    fun removeShelf(id: String) { viewModelScope.launch { comicRepo.removeShelf(id) } }
    fun addToShelf(shelfId: String, item: ComicItem, sourceId: String) { viewModelScope.launch { comicRepo.addToShelf(shelfId, item, sourceId) } }
    fun clearHistory() { viewModelScope.launch { comicRepo.clearHistory() } }

    fun getComicDetail(sourceId: String, item: ComicItem, onResult: (ComicDetail) -> Unit) {
        viewModelScope.launch { onResult(comicRepo.getComicDetail(sourceId, item)) }
    }
    fun getContent(sourceId: String, item: ComicItem, chapter: ComicChapter, onResult: (ComicContent) -> Unit) {
        viewModelScope.launch { onResult(comicRepo.getContent(sourceId, item, chapter)) }
    }
    fun updateReadProgress(item: ComicItem, sourceId: String, chapter: ComicChapter) {
        viewModelScope.launch { comicRepo.updateReadProgress(item, sourceId, chapter) }
    }
}

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoRepo: VideoRepository,
    private val sourceRepo: SourceRepository,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    private val _recommendState = MutableStateFlow<UiState<VideoList>>(UiState())
    val recommendState: StateFlow<UiState<VideoList>> = _recommendState
    private val _searchState = MutableStateFlow<UiState<VideoList>>(UiState())
    val searchState: StateFlow<UiState<VideoList>> = _searchState
    val shelves: StateFlow<List<VideoShelf>> = videoRepo.shelves
    val history: StateFlow<List<VideoHistory>> = videoRepo.history

    init { loadRecommend() }

    fun updateQuery(q: String) { _searchQuery.value = q }
    fun clearSearch() { _searchQuery.value = ""; _searchState.value = UiState() }

    fun loadRecommend() {
        viewModelScope.launch {
            _recommendState.value = UiState.loading()
            try { sourceRepo.loadSources(); _recommendState.value = UiState.success(VideoList()) }
            catch (e: Exception) { _recommendState.value = UiState.error(e.message ?: "????") }
        }
    }

    fun search() {
        val keyword = _searchQuery.value.ifBlank { return }
        viewModelScope.launch {
            _searchState.value = UiState.loading()
            try { _searchState.value = UiState.success(videoRepo.searchAll(keyword, 1)) }
            catch (e: Exception) { _searchState.value = UiState.error(e.message ?: "????") }
        }
    }

    fun loadShelves() { viewModelScope.launch { videoRepo.loadShelves() } }
    fun loadHistory() { viewModelScope.launch { videoRepo.loadHistory() } }
    fun createShelf(name: String) { viewModelScope.launch { videoRepo.createShelf(name) } }
    fun removeShelf(id: String) { viewModelScope.launch { videoRepo.removeShelf(id) } }
    fun addToShelf(shelfId: String, item: VideoItem, sourceId: String) { viewModelScope.launch { videoRepo.addToShelf(shelfId, item, sourceId) } }
    fun clearHistory() { viewModelScope.launch { videoRepo.clearHistory() } }

    fun getVideoDetail(sourceId: String, item: VideoItem, onResult: (VideoDetail) -> Unit) {
        viewModelScope.launch { onResult(videoRepo.getVideoDetail(sourceId, item)) }
    }
    fun getPlayUrl(sourceId: String, item: VideoItem, resource: VideoResource, episode: VideoEpisode, onResult: (VideoUrlMap) -> Unit) {
        viewModelScope.launch { onResult(videoRepo.getPlayUrl(sourceId, item, resource, episode)) }
    }
    fun updateWatchProgress(item: VideoItem, sourceId: String, episode: VideoEpisode, progress: Long) {
        viewModelScope.launch { videoRepo.updateWatchProgress(item, sourceId, episode, progress) }
    }
}
