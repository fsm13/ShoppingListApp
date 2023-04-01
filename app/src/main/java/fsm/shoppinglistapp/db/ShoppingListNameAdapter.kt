package fsm.shoppinglistapp.db

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fsm.shoppinglistapp.R
import fsm.shoppinglistapp.databinding.ListNameItemBinding
import fsm.shoppinglistapp.entities.ShoppingListItem
import fsm.shoppinglistapp.entities.ShoppingListNameItem
import fsm.shoppinglistapp.fragments.ShopListNamesFragment


class ShoppingListNameAdapter(private val listener: Listener) :
    ListAdapter<ShoppingListNameItem, ShoppingListNameAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener)
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListNameItemBinding.bind(view)

        fun setData(shoppingListNameItem: ShoppingListNameItem, listener: Listener) = with(binding) {
            tvListName.text = shoppingListNameItem.name
            tvListTime.text = shoppingListNameItem.time
            pBar.max = shoppingListNameItem.allItemCounter
            pBar.progress = shoppingListNameItem.checkedItemsCounter
            val colorState = ColorStateList.valueOf(getProgressColorState(shoppingListNameItem,
                binding.root.context))
            pBar.progressTintList = colorState
            counterCard.backgroundTintList = colorState
            val counterText = "${shoppingListNameItem.checkedItemsCounter}/" +
                    "${shoppingListNameItem.allItemCounter}"
            tvCounter.text = counterText
            imDelete.setOnClickListener {
                listener.deleteShoppingListName(shoppingListNameItem.id!!)
            }
            itemView.setOnClickListener {
                listener.onClickItem(shoppingListNameItem)
            }
            imEdit.setOnClickListener {
                listener.editShoppingListName(shoppingListNameItem)
            }
        }

        private fun getProgressColorState(item: ShoppingListNameItem, context: Context): Int{
            return if (item.checkedItemsCounter == item.allItemCounter){
                ContextCompat.getColor(context, R.color.green_main)
            } else if (item.checkedItemsCounter >= 0.5 * item.allItemCounter){
                ContextCompat.getColor(context, R.color.yellow)
            } else {
                ContextCompat.getColor(context, R.color.picker_red)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_name_item, parent, false)
                )
            }
        }

    }

    class ItemComparator : DiffUtil.ItemCallback<ShoppingListNameItem>() {
        override fun areItemsTheSame(oldItem: ShoppingListNameItem, newItem: ShoppingListNameItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingListNameItem, newItem: ShoppingListNameItem): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener{
        fun deleteShoppingListName(id: Int)
        fun editShoppingListName(shoppingListNameItem: ShoppingListNameItem)
        fun onClickItem(shoppingListNameItem: ShoppingListNameItem)
    }
}