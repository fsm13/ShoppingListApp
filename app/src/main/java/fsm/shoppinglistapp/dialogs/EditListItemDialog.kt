package fsm.shoppinglistapp.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import fsm.shoppinglistapp.R
import fsm.shoppinglistapp.databinding.EditListItemDialogBinding
import fsm.shoppinglistapp.databinding.NewListDialogBinding
import fsm.shoppinglistapp.entities.ShoppingListItem

object EditListItemDialog {
    fun showDialog(context: Context, item: ShoppingListItem, listener: Listener){
        var dialog: AlertDialog?=null
        val builder = AlertDialog.Builder(context)
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            edName.setText(item.name)
            edInfo.setText(item.itemInfo)
            if (item.itemType == 1) edInfo.visibility = View.GONE
            bUpdate.setOnClickListener {
                if (edName.text.toString().isNotEmpty()){
                    val itemInfo = if (edInfo.text.toString().isEmpty()) ""
                    else edInfo.text.toString()
                    listener.onClick(item.copy(name = edName.text.toString(), itemInfo = itemInfo))

                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }
    interface Listener{
        fun onClick(item: ShoppingListItem)
    }
}