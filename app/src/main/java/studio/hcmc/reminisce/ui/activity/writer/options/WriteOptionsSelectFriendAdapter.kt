package studio.hcmc.reminisce.ui.activity.writer.options

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter

class WriteOptionsSelectFriendAdapter(
    private val itemDelegate: WriteOptionSelectFriendItemViewHolder.Delegate
): Adapter<WriteOptionSelectFriendItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WriteOptionSelectFriendItemViewHolder(parent, itemDelegate)
    override fun getItemCount() = itemDelegate.getItemCount()
    override fun onBindViewHolder(holder: WriteOptionSelectFriendItemViewHolder, position: Int) = holder.bind(itemDelegate.getItem(position))
}
