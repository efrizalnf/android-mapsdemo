package id.zlz.mapsdemo.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.android.libraries.places.api.model.Place
import id.zlz.mapsdemo.repo.BookmarkRepo

class MapsViewModel(aplication: Application) : AndroidViewModel(aplication) {
    private val TAG = "MapsViewModel"

    private var bookmarkRepo : BookmarkRepo = BookmarkRepo(getApplication())

    fun addBookmarkPlace (place : Place, image : Bitmap?){
        val bookmark = bookmarkRepo.createBookmark()
        bookmark.placeid =place.id
        bookmark.name = place.name.toString()
        bookmark.address = place.address.toString()
        bookmark.lattitude = place.latLng?.latitude ?: 0.0
        bookmark.longitude = place.latLng?.longitude ?: 0.0
        bookmark.phone = place.phoneNumber.toString()

        val newId = bookmarkRepo.addBookmark(bookmark)
        
        Log.i(TAG, "new bookmark $newId is created")

    }

}