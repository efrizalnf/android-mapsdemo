import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.zlz.mapsdemo.model.Bookmark

@Database(entities = arrayOf(Bookmark.Bookmark::class), version = 1)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    companion object {
        private var instance: PlaceDatabase? = null
        fun getInstance(context: Context): PlaceDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, PlaceDatabase::class.java, "Place").build()
            }
            return instance as PlaceDatabase
        }
    }
}