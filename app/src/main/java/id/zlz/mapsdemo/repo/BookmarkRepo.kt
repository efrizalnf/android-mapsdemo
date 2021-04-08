package id.zlz.mapsdemo.repo

import BookmarkDao
import android.content.Context
import androidx.lifecycle.LiveData
import id.zlz.mapsdemo.model.Bookmark


class BookmarkRepo(context: Context) {
    //    define instance from place database
    private var db = PlaceDatabase.getInstance(context)
    private var bookmarkDao: BookmarkDao = db.bookmarkDao()

    //   Insert Bookmark
    fun addBookmark(bookmark: Bookmark.Bookmark): Long? {
        val newId = bookmarkDao.insertBookmark(bookmark)
        bookmark.id = newId
        return newId
    }

    //    Create data

    fun createBookmark(): Bookmark {
        return Bookmark()
    }

    //    load data
    val allBookmarks: LiveData<List<Bookmark.Bookmark>>
        get() {
            return bookmarkDao.loadAll()
        }


}