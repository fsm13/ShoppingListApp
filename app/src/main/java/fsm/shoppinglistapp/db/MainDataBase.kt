package fsm.shoppinglistapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fsm.shoppinglistapp.entities.LibraryItem
import fsm.shoppinglistapp.entities.NoteItem
import fsm.shoppinglistapp.entities.ShoppingListItem
import fsm.shoppinglistapp.entities.ShoppingListNameItem

@Database(entities = [LibraryItem::class, NoteItem::class, ShoppingListItem::class,
ShoppingListNameItem::class], version = 1)

abstract class MainDataBase : RoomDatabase() {
    abstract fun getDao() : Dao

    companion object {
        @Volatile
        private var INSTANCE : MainDataBase? = null
        fun getDataBase(context: Context) : MainDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "shopping_list.db"
                ).build()
                instance
            }
        }
    }
}