package id.zlz.mapsdemo.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import id.zlz.mapsdemo.R
import id.zlz.mapsdemo.ui.MapsActivity

class BookmarkInfoWindowsAdapter(context: Activity) : GoogleMap.InfoWindowAdapter {
    private val contents: View

    init {
        contents = context.layoutInflater.inflate(R.layout.content_bookmark_place, null)
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }


    override fun getInfoContents(p0: Marker): View? {
        val title = contents.findViewById<TextView>(R.id.tv_title)
        title.text = p0.title ?: ""

        val datacontent = contents.findViewById<TextView>(R.id.tv_content)
        datacontent.text = p0.snippet ?: ""

        val image = contents.findViewById<ImageView>(R.id.iv_place)
//        image.setImageBitmap(p0?.tag as Bitmap?)
            image.setImageBitmap((p0.tag as MapsActivity.PlaceInfo).image)
        return contents
    }


}