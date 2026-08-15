package com.zarnth.savr.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zarnth.savr.domain.model.Bookmark
import com.zarnth.savr.domain.model.Collection
import com.zarnth.savr.domain.repository.BookmarkRepository
import com.zarnth.savr.domain.model.SortOrder
import com.zarnth.savr.link_fetcher.LinkMetadataParser
import com.zarnth.savr.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CollectionViewModel(
    private val repository: BookmarkRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CollectionState())
    val state = _state.asStateFlow()
    private var collectionJob: Job? = null
    private var rawCollectionBookmarks: List<Bookmark> = emptyList()
    private val parser = LinkMetadataParser()

    init {
        loadCollections()
    }

    fun onEvent(event: CollectionEvents) {
        when (event) {
            is CollectionEvents.InputNameChanged -> {
                _state.update { it.copy(inputName = event.name) }
            }

            CollectionEvents.ShowCreateDialog -> {
                _state.update { it.copy(showCreateDialog = true, inputName = "") }
            }

            CollectionEvents.HideCreateDialog -> {
                _state.update { it.copy(showCreateDialog = false, inputName = "") }
            }

            CollectionEvents.CreateCollection -> {
                createCollection()
            }

            is CollectionEvents.SelectCollection -> {
                selectCollection(event.collection)
            }

            is CollectionEvents.RestoreCollectionDetail -> {
                restoreCollectionDetail(event.collectionId)
            }

            is CollectionEvents.ToggleSelection -> {
                val current = _state.value
                val newSelected = if (event.id in current.selectedIds) {
                    current.selectedIds - event.id
                } else {
                    current.selectedIds + event.id
                }
                _state.update {
                    it.copy(
                        selectedIds = newSelected,
                        isSelectionMode = newSelected.isNotEmpty()
                    )
                }
            }

            CollectionEvents.ClearSelection -> {
                _state.update { it.copy(selectedIds = emptySet(), isSelectionMode = false) }
            }

            CollectionEvents.SelectAll -> {
                val allIds = _state.value.collections.map { it.id }.toSet()
                _state.update {
                    it.copy(
                        selectedIds = allIds,
                        isSelectionMode = allIds.isNotEmpty()
                    )
                }
            }

            CollectionEvents.DeselectAll -> {
                _state.update { it.copy(selectedIds = emptySet(), isSelectionMode = false) }
            }

            CollectionEvents.DeleteSelected -> {
                deleteSelected()
            }

            is CollectionEvents.DeleteCollectionById -> {
                deleteCollectionById(event.collectionId)
            }

            is CollectionEvents.ShowDetailBodySheet -> {
                _state.update { it.copy(tempBookmark = event.bookmark, isDetailBodySheet = true) }
            }

            CollectionEvents.DismissDetailBodySheet -> {
                _state.update { it.copy(tempBookmark = null, isDetailBodySheet = false) }
            }

            is CollectionEvents.ShowEditBookmarkSheet -> {
                _state.update {
                    it.copy(
                        isDetailBodySheet = false,
                        editingBookmark = event.bookmark,
                        editTitle = event.bookmark.title ?: "",
                        editDescription = event.bookmark.description ?: "",
                        isEditBookmarkSheet = true
                    )
                }
            }

            CollectionEvents.HideEditBookmarkSheet -> {
                _state.update {
                    it.copy(
                        isEditBookmarkSheet = false,
                        editingBookmark = null,
                        editTitle = "",
                        editDescription = ""
                    )
                }
            }

            is CollectionEvents.EditTitleChanged -> {
                _state.update { it.copy(editTitle = event.text) }
            }

            is CollectionEvents.EditDescriptionChanged -> {
                _state.update { it.copy(editDescription = event.text) }
            }

            CollectionEvents.SaveEditedBookmark -> {
                saveEditedBookmark()
            }

            is CollectionEvents.ToggleDetailSelection -> {
                val current = _state.value
                val newSelected = if (event.id in current.detailSelectedIds) {
                    current.detailSelectedIds - event.id
                } else {
                    current.detailSelectedIds + event.id
                }
                _state.update {
                    it.copy(
                        detailSelectedIds = newSelected,
                        isDetailSelectionMode = newSelected.isNotEmpty()
                    )
                }
            }

            CollectionEvents.ClearDetailSelection -> {
                _state.update { it.copy(detailSelectedIds = emptySet(), isDetailSelectionMode = false) }
            }

            CollectionEvents.SelectAllDetail -> {
                val allIds = _state.value.collectionBookmarks.map { it.id }.toSet()
                _state.update {
                    it.copy(
                        detailSelectedIds = allIds,
                        isDetailSelectionMode = allIds.isNotEmpty()
                    )
                }
            }

            CollectionEvents.DeselectAllDetail -> {
                _state.update { it.copy(detailSelectedIds = emptySet(), isDetailSelectionMode = false) }
            }

            is CollectionEvents.RemoveSelectedFromCollection -> {
                removeSelectedFromCollection(event.collectionId)
            }

            is CollectionEvents.SetSortOrder -> {
                _state.update {
                    it.copy(
                        sortOrder = event.sortOrder,
                        showSortSheet = false,
                        collectionBookmarks = sortBookmarks(rawCollectionBookmarks, event.sortOrder)
                    )
                }
            }

            CollectionEvents.ShowSortSheet -> {
                _state.update { it.copy(showSortSheet = true) }
            }

            CollectionEvents.HideSortSheet -> {
                _state.update { it.copy(showSortSheet = false) }
            }

            CollectionEvents.ShowAddBookmarkSheet -> {
                _state.update { it.copy(showAddBookmarkSheet = true, inputUrl = "") }
            }

            CollectionEvents.HideAddBookmarkSheet -> {
                _state.update { it.copy(showAddBookmarkSheet = false, inputUrl = "") }
            }

            is CollectionEvents.AddBookmarkUrlChanged -> {
                _state.update { it.copy(inputUrl = event.url) }
            }

            CollectionEvents.AddBookmarkToCollection -> {
                addBookmarkToCollection()
            }

            CollectionEvents.CollectionDuplicateToastShown -> {
                _state.update { it.copy(duplicateToastKey = 0) }
            }

            is CollectionEvents.AddClipboardToCollection -> {
                addClipboardToCollection(event.url, event.title, event.description, event.imageUrl)
            }
        }
    }

    fun backToCollections() {
        collectionJob?.cancel()
        _state.update { it.copy(selectedCollection = null, collectionBookmarks = emptyList()) }
    }

    private fun restoreCollectionDetail(collectionId: Long) {
        if (_state.value.selectedCollection?.id == collectionId) return
        val found = _state.value.collections.find { it.id == collectionId }
        if (found != null) {
            selectCollection(found)
        }
    }

    private fun selectCollection(collection: Collection) {
        collectionJob?.cancel()
        _state.update { it.copy(selectedCollection = collection, collectionBookmarks = emptyList(), isDetailLoading = true) }
        collectionJob = viewModelScope.launch {
            repository.getBookmarksInCollection(collection.id).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isDetailLoading = true) }
                    is Resource.Error -> _state.update { it.copy(isDetailLoading = false, error = resource.errorMessage ?: "Error") }
                    is Resource.Success -> {
                        val items = resource.data ?: emptyList()
                        rawCollectionBookmarks = items
                        val sortOrder = _state.value.sortOrder
                        _state.update { it.copy(isDetailLoading = false, collectionBookmarks = sortBookmarks(items, sortOrder)) }
                    }
                }
            }
        }
    }

    private fun saveEditedBookmark() {
        val bookmark = _state.value.editingBookmark ?: return
        val title = _state.value.editTitle.trim().ifBlank { null }
        val description = _state.value.editDescription.trim().ifBlank { null }
        viewModelScope.launch {
            try {
                repository.updateTitleAndDescription(bookmark.id, title, description)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Update failed") }
            }
        }
    }

    private fun createCollection() {
        val name = _state.value.inputName.trim()
        if (name.isEmpty()) return
        viewModelScope.launch {
            repository.createCollection(name)
            _state.update { it.copy(showCreateDialog = false, inputName = "") }
        }
    }

    private fun deleteSelected() {
        val selected = _state.value.collections.filter { it.id in _state.value.selectedIds }
        if (selected.isEmpty()) return
        viewModelScope.launch {
            selected.forEach { repository.deleteCollection(it) }
            _state.update { it.copy(selectedIds = emptySet(), isSelectionMode = false) }
        }
    }

    private fun deleteCollectionById(collectionId: Long) {
        viewModelScope.launch {
            val collection = _state.value.collections.find { it.id == collectionId } ?: return@launch
            repository.deleteCollection(collection)
            backToCollections()
        }
    }

    private fun removeSelectedFromCollection(collectionId: Long) {
        val ids = _state.value.detailSelectedIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            ids.forEach { repository.removeBookmarkFromCollection(it, collectionId) }
            _state.update { it.copy(detailSelectedIds = emptySet(), isDetailSelectionMode = false) }
        }
    }

    private fun addBookmarkToCollection() {
        val collection = _state.value.selectedCollection ?: return
        val rawUrl = _state.value.inputUrl.trim()
        if (rawUrl.isEmpty()) return
        val url = if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            "https://$rawUrl"
        } else {
            rawUrl
        }
        viewModelScope.launch {
            try {
                if (repository.isUrlInCollection(url, collection.id)) {
                    _state.update {
                        it.copy(
                            showAddBookmarkSheet = false,
                            inputUrl = "",
                            duplicateToastKey = it.duplicateToastKey + 1
                        )
                    }
                    return@launch
                }
                _state.update { it.copy(isAddBookmarkLoading = true) }
                val meta = parser.parse(url)
                val bookmarkUrl = meta?.url ?: url
                if (repository.isUrlInCollection(bookmarkUrl, collection.id)) {
                    _state.update {
                        it.copy(
                            isAddBookmarkLoading = false,
                            showAddBookmarkSheet = false,
                            inputUrl = "",
                            duplicateToastKey = it.duplicateToastKey + 1
                        )
                    }
                    return@launch
                }
                val bookmark = Bookmark(
                    url = bookmarkUrl,
                    title = meta?.title,
                    description = meta?.description,
                    imageUrl = meta?.imageUrl,
                    isCollectionOnly = true
                )
                repository.insert(bookmark)
                val bookmarkId = repository.getBookmarkIdByUrl(bookmarkUrl)
                if (bookmarkId != null) {
                    repository.addBookmarkToCollection(bookmarkId, collection.id)
                }
                _state.update {
                    it.copy(
                        isAddBookmarkLoading = false,
                        showAddBookmarkSheet = false,
                        inputUrl = ""
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Unknown error",
                        isAddBookmarkLoading = false
                    )
                }
            }
        }
    }

    private fun addClipboardToCollection(url: String, title: String?, description: String?, imageUrl: String?) {
        val collection = _state.value.selectedCollection ?: return
        if (url.isBlank()) return
        viewModelScope.launch {
            try {
                if (repository.isUrlInCollection(url, collection.id)) {
                    _state.update { it.copy(duplicateToastKey = it.duplicateToastKey + 1) }
                    return@launch
                }
                _state.update { it.copy(isAddBookmarkLoading = true) }
                repository.insert(
                    Bookmark(
                        url = url,
                        title = title,
                        description = description,
                        imageUrl = imageUrl,
                        isCollectionOnly = true
                    )
                )
                val bookmarkId = repository.getBookmarkIdByUrl(url)
                if (bookmarkId != null) {
                    repository.addBookmarkToCollection(bookmarkId, collection.id)
                }
                _state.update { it.copy(isAddBookmarkLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Unknown error",
                        isAddBookmarkLoading = false
                    )
                }
            }
        }
    }

    private fun loadCollections() {
        viewModelScope.launch {
            repository.getAllCollections().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = resource.errorMessage ?: "Error") }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, collections = resource.data ?: emptyList()) }
                }
            }
        }
    }

    private fun sortBookmarks(bookmarks: List<Bookmark>, sortOrder: SortOrder): List<Bookmark> {
        return when (sortOrder) {
            SortOrder.DATE_NEWEST -> bookmarks.sortedByDescending { it.createdAt }
            SortOrder.DATE_OLDEST -> bookmarks.sortedBy { it.createdAt }
            SortOrder.TITLE_ASC -> bookmarks.sortedBy { it.title?.lowercase() }
            SortOrder.TITLE_DESC -> bookmarks.sortedByDescending { it.title?.lowercase() }
        }
    }
}
