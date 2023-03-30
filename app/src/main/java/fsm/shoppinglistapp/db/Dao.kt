package fsm.shoppinglistapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fsm.shoppinglistapp.entities.NoteItem
import fsm.shoppinglistapp.entities.ShoppingListNameItem
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM note_list")
    fun getAllNotes() : Flow<List<NoteItem>>
    @Query("SELECT * FROM shopping_list_names")
    fun getAllShoppingListNames() : Flow<List<ShoppingListNameItem>>
    @Query("DELETE FROM note_list WHERE id IS :id")
    suspend fun deleteNote(id: Int)
    @Query("DELETE FROM shopping_list_names WHERE id IS :id")
    suspend fun deleteShoppingListName(id: Int)
    @Insert
    suspend fun insertNote(note : NoteItem)
    @Insert
    suspend fun insertShopListName(name : ShoppingListNameItem)
    @Update
    suspend fun updateNote(note : NoteItem)
    @Update
    suspend fun updateShoppingListName(shoppingListNameItem : ShoppingListNameItem)
}