package studio.hcmc.reminisce.ui.activity.setting

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.vo.friend.FriendVO

class FriendListAdapter(
    private val adapterDelegate: SingleTypeAdapterDelegate<FriendVO>,
    private val itemDelegate: FriendListViewHolder.Delegate
): Adapter<FriendListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FriendListViewHolder(parent, itemDelegate)
    override fun getItemCount() = adapterDelegate.getItemCount()
    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) = holder.bind(adapterDelegate.getItem(position))
}