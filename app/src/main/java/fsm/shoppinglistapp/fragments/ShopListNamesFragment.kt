package fsm.shoppinglistapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import fsm.shoppinglistapp.activities.MainApp
import fsm.shoppinglistapp.activities.ShoppingListActivity
import fsm.shoppinglistapp.databinding.FragmentShopListNamesBinding
import fsm.shoppinglistapp.db.MainViewModel
import fsm.shoppinglistapp.db.ShoppingListNameAdapter
import fsm.shoppinglistapp.dialogs.DeleteDialog
import fsm.shoppinglistapp.dialogs.NewListDialog
import fsm.shoppinglistapp.entities.ShoppingListNameItem
import fsm.shoppinglistapp.utils.TimeManager

class ShopListNamesFragment : BaseFragment(), ShoppingListNameAdapter.Listener{
    private lateinit var binding: FragmentShopListNamesBinding
    private lateinit var adapter: ShoppingListNameAdapter

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener{
            override fun onClick(name: String) {
                val shopListName = ShoppingListNameItem(
                    null,
                    name,
                    TimeManager.getCurrentTime(),
                    0,
                    0,
                    ""
                )
                mainViewModel.insertShopListName(shopListName)
            }
        }, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopListNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    private fun initRcView()= with(binding){
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = ShoppingListNameAdapter(this@ShopListNamesFragment)
        rcView.adapter = adapter
    }

    private fun observer(){
        mainViewModel.allItemShoppingListNames.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }

    override fun deleteShoppingListName(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                mainViewModel.deleteShoppingList(id, true)
            }
        })
    }

    override fun editShoppingListName(shoppingListNameItem: ShoppingListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener{
            override fun onClick(name: String) {
                mainViewModel.updateShoppingListName(shoppingListNameItem.copy(name = name))
            }
        }, shoppingListNameItem.name)
    }

    override fun onClickItem(shoppingListNameItem: ShoppingListNameItem) {
        val i = Intent(activity, ShoppingListActivity::class.java).apply {
            putExtra(ShoppingListActivity.SHOPPING_LIST_NAME, shoppingListNameItem)
        }
        startActivity(i)
    }
}