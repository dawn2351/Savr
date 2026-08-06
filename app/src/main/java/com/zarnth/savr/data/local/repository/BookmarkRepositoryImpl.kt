package com.zarnth.savr.data.local.repository

import com.zarnth.savr.data.local.dao.BookmarkDao
import com.zarnth.savr.data.local.dao.CollectionDao
import com.zarnth.savr.data.local.entity.BookmarkCollectionCrossRef
import com.zarnth.savr.data.local.mapper.toDomain
import com.zarnth.savr.data.local.mapper.toEntity
import com.zarnth.savr.domain.model.Bookmark
import com.zarnth.savr.domain.model.Collection
import com.zarnth.savr.domain.repository.BookmarkRepository
import com.zarnth.savr.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class BookmarkRepositoryImpl(
    private val dao: BookmarkDao,
    private val collectionDao: CollectionDao
) : BookmarkRepository {

    override suspend fun insert(bookmark: Bookmark): Boolean {
        return dao.insertOrUnhide(bookmark.toEntity())
    }

    override suspend fun existsByUrl(url: String): Boolean {
        return dao.existsByUrl(url)
    }

    override suspend fun getBookmarksWithoutImage(): List<Bookmark> {
        return dao.getBookmarksWithoutImageOnce().map { it.toDomain() }
    }

    override suspend fun hideBookmarks(ids: List<Long>) {
        dao.hideBookmarks(ids)
    }

    override suspend fun searchBookmarks(text: String): Flow<Resource<List<Bookmark>>> {
        return dao.searchBookmarks(text)
            .map { list -> Resource.Success(list.map { it.toDomain() }) as Resource<List<Bookmark>> }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override fun getBookmarks(): Flow<Resource<List<Bookmark>>> {
        return dao.getBookmarks()
            .map { list -> Resource.Success(list.map { it.toDomain() }) as Resource<List<Bookmark>> }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override suspend fun createCollection(name: String): Long {
        return collectionDao.insertCollection(
            com.zarnth.savr.data.local.entity.CollectionEntity(name = name)
        )
    }

    override suspend fun deleteCollection(collection: Collection) {
        collectionDao.deleteCollection(collection.toEntity())
    }

    override fun getAllCollections(): Flow<Resource<List<Collection>>> {
        return collectionDao.getAllCollections()
            .flatMapLatest { collections ->
                flow {
                    val result = collections.map { c ->
                        Collection(
                            id = c.id,
                            name = c.name,
                            bookmarkCount = c.bookmarkCount,
                            previewUrls = collectionDao.getPreviewUrlsForCollection(c.id)
                        )
                    }
                    emit(Resource.Success(result) as Resource<List<Collection>>)
                }
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override fun getBookmarksInCollection(collectionId: Long): Flow<Resource<List<Bookmark>>> {
        return collectionDao.getBookmarksInCollection(collectionId)
            .map { list ->
                Resource.Success(list.map { it.toDomain() }) as Resource<List<Bookmark>>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override suspend fun addBookmarkToCollection(bookmarkId: Long, collectionId: Long) {
        collectionDao.addBookmarkToCollection(BookmarkCollectionCrossRef(bookmarkId, collectionId))
    }

    override suspend fun addBookmarksToCollection(bookmarkIds: List<Long>, collectionId: Long) {
        collectionDao.addBookmarksToCollection(bookmarkIds.map { BookmarkCollectionCrossRef(it, collectionId) })
    }

    override suspend fun removeBookmarkFromCollection(bookmarkId: Long, collectionId: Long) {
        collectionDao.removeBookmarkFromCollection(BookmarkCollectionCrossRef(bookmarkId, collectionId))
    }

    override suspend fun updateImageUrl(id: Long, imageUrl: String?) {
        dao.updateImageUrl(id, imageUrl)
    }
}
