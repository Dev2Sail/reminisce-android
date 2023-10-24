package studio.hcmc.reminisce.ui.activity.setting

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.vo.friend.FriendVO

class FriendsAdapter(
    private val adapterDelegate: SingleTypeAdapterDelegate<FriendVO>,
    private val itemDelegate: FriendsItemViewHolder.Delegate
): Adapter<FriendsItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FriendsItemViewHolder(parent, itemDelegate)
    override fun getItemCount() = adapterDelegate.getItemCount()
    override fun onBindViewHolder(holder: FriendsItemViewHolder, position: Int) = holder.bind(adapterDelegate.getItem(position))
}