package studio.hcmc.reminisce.ui.activity.setting

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter

class FriendSettingAdapter(
    private val itemDelegate: FriendSettingItemViewHolder.Delegate
): Adapter<FriendSettingItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FriendSettingItemViewHolder(parent, itemDelegate)
    override fun getItemCount() = itemDelegate.getItemCount()
    override fun onBindViewHolder(holder: FriendSettingItemViewHolder, position: Int) = holder.bind(itemDelegate.getItem(position))
}

/*
no_position = -1
 */