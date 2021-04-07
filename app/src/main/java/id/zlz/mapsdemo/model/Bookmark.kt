package id.zlz.mapsdemo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

class Bookmark {

    @Entity
    data class Bookmark(
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,
        var placeid: String? = "",
        var name: String? = "",
        var address: String? = "",
        var lattitude: Double? = 0.0,
        var longitude: Double? = 0.0,
        var phone:String = ""
    )
}