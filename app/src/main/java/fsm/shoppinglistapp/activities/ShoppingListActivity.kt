package fsm.shoppinglistapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.util.copy
import fsm.shoppinglistapp.R
import fsm.shoppinglistapp.databinding.ActivityShoppingListBinding
import fsm.shoppinglistapp.db.MainViewModel
import fsm.shoppinglistapp.db.ShoppingListItemAdapter
import fsm.shoppinglistapp.dialogs.EditListItemDialog
import fsm.shoppinglistapp.entities.LibraryItem
import fsm.shoppinglistapp.entities.ShoppingListItem
import fsm.shoppinglistapp.entities.ShoppingListNameItem
import fsm.shoppinglistapp.utils.ShareHelper

class ShoppingListActivity : AppCompatActivity(), ShoppingListItemAdapter.Listener {
    private lateinit var binding: ActivityShoppingListBinding
    private var shoppingListNameItem: ShoppingListNameItem? = null
    private lateinit var saveItem: MenuItem
    private var edItem: EditText? = null
    private var adapter: ShoppingListItemAdapter? = null
    private lateinit var textWatcher: TextWatcher
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        listItemObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        saveItem = menu?.findItem(R.id.save_item)!!
        val newItem = menu.findItem(R.id.new_item)
        edItem = newItem.actionView?.findViewById(R.id.edNewShopItem) as EditText
        newItem.setOnActionExpandListener(expandActionView())
        saveItem.isVisible = false
        textWatcher = textWatcher()
        return true
    }

    private fun textWatcher(): TextWatcher{
        return object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainViewModel.getAllLibraryItems("%$s%")
            }

            override fun afterTextChanged(s: Editable?) {

            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_item -> addNewShopItem(edItem?.text.toString())
            R.id.delete_list -> {
                mainViewModel.deleteShoppingList(shoppingListNameItem?.id!!, true)
                finish()
            }
            R.id.clear_list -> mainViewModel.deleteShoppingList(shoppingListNameItem?.id!!, false)
            R.id.share_list -> startActivity(
                Intent.createChooser(
                    ShareHelper.shareShoppingList(adapter?.currentList!!, shoppingListNameItem?.name!!),
                    "Share by"))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name: String){
        if (name.isEmpty()) return
         val item = ShoppingListItem(
             id = null,
             name,
             "",
             false,
             shoppingListNameItem?.id!!,
             0
         )
        edItem?.setText("")
        mainViewModel.insertShoppingListItem(item)
    }

    private fun listItemObserver(){
        mainViewModel.getAllItemsFromList(shoppingListNameItem?.id!!).observe(this, {
            adapter?.submitList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun libraryItemObserver(){
        mainViewModel.libraryItems.observe(this) {
            val tempShoppingList = ArrayList<ShoppingListItem>()
            it.forEach { item ->
                val shoppingItem = ShoppingListItem(
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    1
                )
                tempShoppingList.add(shoppingItem)
            }
            adapter?.submitList(tempShoppingList)
            binding.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun initRcView() = with(binding){
        adapter = ShoppingListItemAdapter(this@ShoppingListActivity)
        rcView.layoutManager = LinearLayoutManager(this@ShoppingListActivity)
        rcView.adapter = adapter
    }

    private fun expandActionView(): MenuItem.OnActionExpandListener{
        return object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                saveItem.isVisible = true
                edItem?.addTextChangedListener(textWatcher)
                libraryItemObserver()
                mainViewModel.getAllItemsFromList(shoppingListNameItem?.id!!).
                removeObservers(this@ShoppingListActivity)
                mainViewModel.getAllLibraryItems("%%")
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                saveItem.isVisible = false
                edItem?.removeTextChangedListener(textWatcher)
                invalidateOptionsMenu()
                mainViewModel.libraryItems.removeObservers(this@ShoppingListActivity)
                edItem?.setText("")
                listItemObserver()
                return true
            }
        }
    }

    private fun init(){
        shoppingListNameItem = intent.getSerializableExtra(SHOPPING_LIST_NAME) as ShoppingListNameItem
    }

    companion object{
        const val SHOPPING_LIST_NAME = "shopping_list_name"
    }

    override fun onClickItem(shoppingListItem: ShoppingListItem, state: Int) {
        when(state){
            ShoppingListItemAdapter.CHECK_BOX -> mainViewModel.updateItem(shoppingListItem)
            ShoppingListItemAdapter.EDIT -> editListItem(shoppingListItem)
            ShoppingListItemAdapter.EDIT_LIBRARY_ITEM -> editLibraryItem(shoppingListItem)
            ShoppingListItemAdapter.DELETE_LIBRARY_ITEM -> {
                mainViewModel.deleteLibraryItem(shoppingListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
            ShoppingListItemAdapter.ADD -> addNewShopItem(shoppingListItem.name)
        }
    }

    private fun editListItem(item:ShoppingListItem){
        EditListItemDialog.showDialog(this, item, object : EditListItemDialog.Listener{
            override fun onClick(item: ShoppingListItem) {
                mainViewModel.updateItem(item)
            }

        })

    }

    private fun editLibraryItem(item:ShoppingListItem){
        EditListItemDialog.showDialog(this, item, object : EditListItemDialog.Listener{
            override fun onClick(item: ShoppingListItem) {
                mainViewModel.updateLibraryItem(LibraryItem(item.id, item.name))
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
        })
    }

    private fun saveItemCount(){
        var checkedItemCounter = 0
        adapter?.currentList?.forEach{
            if (it.itemChecked) checkedItemCounter++
        }
        val tempShoppingListNameItem = shoppingListNameItem?.copy(
            allItemCounter = adapter?.itemCount!!,
            checkedItemsCounter = checkedItemCounter
        )
        mainViewModel.updateShoppingListName(tempShoppingListNameItem!!)
    }

    override fun onBackPressed(){
        saveItemCount()
        super.onBackPressed()
    }
}