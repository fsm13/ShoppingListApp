package fsm.shoppinglistapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fsm.shoppinglistapp.entities.LibraryItem
import fsm.shoppinglistapp.entities.NoteItem
import fsm.shoppinglistapp.entities.ShoppingListItem
import fsm.shoppinglistapp.entities.ShoppingListNameItem
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM note_list")
    fun getAllNotes() : Flow<List<NoteItem>>
    @Query("SELECT * FROM shopping_list_names")
    fun getAllShoppingListNames() : Flow<List<ShoppingListNameItem>>
    @Query("SELECT * FROM shopping_list_item WHERE listId LIKE :listId")
    fun getAllShoppingListItems(listId: Int) : Flow<List<ShoppingListItem>>

    @Query("SELECT * FROM library WHERE name LIKE :name")
    suspend fun getAllLibraryItems(name: String) : List<LibraryItem>

    @Query("DELETE FROM shopping_list_item WHERE listId LIKE :listId")
    suspend fun deleteShopItemsByListId(listId: Int)
    @Query("DELETE FROM note_list WHERE id IS :id")
    suspend fun deleteNote(id: Int)
    @Query("DELETE FROM shopping_list_names WHERE id IS :id")
    suspend fun deleteShoppingListName(id: Int)
    @Insert
    suspend fun insertNote(note : NoteItem)
    @Insert
    suspend fun insertItem(shoppingListItem : ShoppingListItem)
    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)
    @Query("DELETE FROM library WHERE id IS :id")
    suspend fun deleteLibraryItem(id: Int)
    @Insert
    suspend fun insertShopListName(name : ShoppingListNameItem)
    @Update
    suspend fun updateNote(note : NoteItem)
    @Update
    suspend fun updateLibraryItem(libraryItem: LibraryItem)
    @Update
    suspend fun updateItem(shoppingListItem: ShoppingListItem)
    @Update
    suspend fun updateShoppingListName(shoppingListNameItem : ShoppingListNameItem)
}