package fsm.shoppinglistapp.db

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fsm.shoppinglistapp.R

import fsm.shoppinglistapp.databinding.ShoppingListItemBinding
import fsm.shoppinglistapp.databinding.ShoppingListLibraryItemBinding
import fsm.shoppinglistapp.entities.ShoppingListItem



class ShoppingListItemAdapter(private val listener: Listener) :
    ListAdapter<ShoppingListItem, ShoppingListItemAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == 0) ItemHolder.createShopItem(parent)
        else ItemHolder.createLibraryItem(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (getItem(position).itemType == 0) {
            holder.setItemData(getItem(position), listener)
        } else {
            holder.setLibraryData(getItem(position), listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun setItemData(shoppingListItem: ShoppingListItem, listener: Listener){
            val binding = ShoppingListItemBinding.bind(view)
            binding.apply {
                tvName.text = shoppingListItem.name
                tvInfo.text = shoppingListItem.itemInfo
                tvInfo.visibility = infoVisibility(shoppingListItem)
                chBox.isChecked = shoppingListItem.itemChecked
                setPaintFlagAndColor()
                chBox.setOnClickListener {
                    listener.onClickItem(
                        shoppingListItem.copy(itemChecked = chBox.isChecked), CHECK_BOX)
                }
                imEdit.setOnClickListener {
                    listener.onClickItem(shoppingListItem, EDIT)
                }
            }

        }

        fun setLibraryData(shoppingListItem: ShoppingListItem, listener: Listener){
            val binding = ShoppingListLibraryItemBinding.bind(view)
            binding.apply {
                tvName.text = shoppingListItem.name
                imEdit.setOnClickListener {
                    listener.onClickItem(shoppingListItem, EDIT_LIBRARY_ITEM)
                }
                imDelete.setOnClickListener {
                    listener.onClickItem(shoppingListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener {
                    listener.onClickItem(shoppingListItem, ADD)
                }
            }

        }

        private fun setPaintFlagAndColor(){
            val binding = ShoppingListItemBinding.bind(view)
            binding.apply{
                if (chBox.isChecked){
                    tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey))
                } else {
                    tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                }
            }
        }

        private fun infoVisibility(shoppingListItem: ShoppingListItem): Int{
            return if (shoppingListItem.itemInfo.isNullOrEmpty()) View.GONE else View.VISIBLE

        }

        companion object {
            fun createShopItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shopping_list_item, parent, false)
                )
            }
            fun createLibraryItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shopping_list_library_item, parent, false)
                )
            }
        }

    }

    class ItemComparator : DiffUtil.ItemCallback<ShoppingListItem>() {
        override fun areItemsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener{
        fun onClickItem(shoppingListItem: ShoppingListItem, state: Int)
    }

    companion object{
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD = 4
    }
}