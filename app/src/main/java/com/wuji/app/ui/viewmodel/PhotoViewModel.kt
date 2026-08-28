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

data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    companion object {
        fun <T> loading() = UiState<T>(isLoading = true)
        fun <T> success(data: T) = UiState(data = data, isLoading = false)
        fun <T> error(msg: String) = UiState<T>(error = msg, isLoading = false)
    }
}

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val photoRepo: PhotoRepository,
    private val sourceRepo: SourceRepository,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _recommendState = MutableStateFlow<UiState<PhotoList>>(UiState())
    val recommendState: StateFlow<UiState<PhotoList>> = _recommendState

    private val _searchState = MutableStateFlow<UiState<PhotoList>>(UiState())
    val searchState: StateFlow<UiState<PhotoList>> = _searchState

    private val _page = MutableStateFlow(1)

    init { loadRecommend() }

    fun updateQuery(q: String) { _searchQuery.value = q }
    fun clearSearch() { _searchQuery.value = ""; _searchState.value = UiState() }

    fun loadRecommend() {
        viewModelScope.launch {
            _recommendState.value = UiState.loading()
            try {
                sourceRepo.loadSources()
                val result = photoRepo.getRecommendAll(1)
                _recommendState.value = UiState.success(result)
            } catch (e: Exception) {
                _recommendState.value = UiState.error(e.message ?: "????")
            }
        }
    }

    fun loadMoreRecommend() {
        viewModelScope.launch {
            val current = _recommendState.value.data ?: return@launch
            try {
                val next = _page.value + 1
                val result = photoRepo.getRecommendAll(next)
                _page.value = next
                _recommendState.value = UiState.success(current.copy(list = current.list + result.list, total = result.total, page = next))
            } catch (e: Exception) { Timber.e(e) }
        }
    }

    fun search() {
        val keyword = _searchQuery.value.ifBlank { return }
        viewModelScope.launch {
            _searchState.value = UiState.loading()
            try {
                val result = photoRepo.searchAll(keyword, 1)
                _searchState.value = UiState.success(result)
            } catch (e: Exception) {
                _searchState.value = UiState.error(e.message ?: "????")
            }
        }
    }

    fun loadMoreSearch() {
        val keyword = _searchQuery.value.ifBlank { return }
        viewModelScope.launch {
            val current = _searchState.value.data ?: return@launch
            try {
                val next = current.page + 1
                val result = photoRepo.searchAll(keyword, next)
                _searchState.value = UiState.success(current.copy(list = current.list + result.list, page = next))
            } catch (e: Exception) { Timber.e(e) }
        }
    }

    fun loadShelves() { viewModelScope.launch { photoRepo.loadShelves() } }
    val shelves: StateFlow<List<PhotoShelf>> = photoRepo.shelves

    fun getPhotoDetail(sourceId: String, item: PhotoItem, page: Int, onResult: (PhotoDetail) -> Unit) {
        viewModelScope.launch {
            try { onResult(photoRepo.getPhotoDetail(sourceId, item, page)) }
            catch (e: Exception) { Timber.e(e) }
        }
    }

    fun createShelf(name: String) { viewModelScope.launch { photoRepo.createShelf(name) } }
    fun removeShelf(id: String) { viewModelScope.launch { photoRepo.removeShelf(id) } }
    fun addToShelf(shelfId: String, item: PhotoItem, sourceId: String) {
        viewModelScope.launch { photoRepo.addToShelf(shelfId, item, sourceId) }
    }
    fun removeFromShelf(shelfId: String, itemId: String) {
        viewModelScope.launch { photoRepo.removeFromShelf(shelfId, itemId) }
    }
}
