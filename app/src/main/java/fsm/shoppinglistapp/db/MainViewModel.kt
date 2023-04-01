package fsm.shoppinglistapp.db

import androidx.lifecycle.*
import fsm.shoppinglistapp.entities.LibraryItem
import fsm.shoppinglistapp.entities.NoteItem
import fsm.shoppinglistapp.entities.ShoppingListItem
import fsm.shoppinglistapp.entities.ShoppingListNameItem
import kotlinx.coroutines.launch

class MainViewModel(database: MainDataBase) : ViewModel() {
    val dao = database.getDao()
    val libraryItems = MutableLiveData<List<LibraryItem>>()
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()
    val allItemShoppingListNames: LiveData<List<ShoppingListNameItem>> =
        dao.getAllShoppingListNames().asLiveData()

    fun getAllItemsFromList(listId: Int): LiveData<List<ShoppingListItem>>{
        return dao.getAllShoppingListItems(listId).asLiveData()
    }

    fun getAllLibraryItems(name: String)= viewModelScope.launch{
        libraryItems.postValue(dao.getAllLibraryItems(name))
    }

    fun insertNote(note: NoteItem) = viewModelScope.launch{
        dao.insertNote(note)
    }

    fun updateNote(note: NoteItem) = viewModelScope.launch{
        dao.updateNote(note)
    }

    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch{
        dao.updateLibraryItem(item)
    }

    fun updateItem(shoppingListItem: ShoppingListItem) = viewModelScope.launch{
        dao.updateItem(shoppingListItem)
    }

    fun insertShopListName(listName: ShoppingListNameItem) = viewModelScope.launch{
        dao.insertShopListName(listName)
    }

    fun insertShoppingListItem(shoppingListItem : ShoppingListItem) = viewModelScope.launch{
        dao.insertItem(shoppingListItem)
        if (!isLibraryItemExists(shoppingListItem.name))
            dao.insertLibraryItem(LibraryItem(null, shoppingListItem.name))
    }

    fun deleteNote(id: Int) = viewModelScope.launch{
        dao.deleteNote(id)
    }

    fun deleteLibraryItem(id: Int) = viewModelScope.launch{
        dao.deleteLibraryItem(id)
    }

    fun deleteShoppingList(id: Int, deleteList: Boolean) = viewModelScope.launch{
        if (deleteList) dao.deleteShoppingListName(id)
        dao.deleteShopItemsByListId(id)
    }

    fun updateShoppingListName(shoppingListNameItem: ShoppingListNameItem) = viewModelScope.launch{
        dao.updateShoppingListName(shoppingListNameItem)
    }

    private suspend fun isLibraryItemExists(name: String): Boolean{
        return dao.getAllLibraryItems(name).isNotEmpty()
    }


    class MainViewModelFactory(val database: MainDataBase) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UCHECKED_CAST")
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}