package studio.hcmc.reminisce.ui.activity.setting

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate

class FriendsAdapter(
    private val adapterDelegate: Delegate,
    private val itemDelegate: FriendsItemViewHolder.Delegate
): Adapter<FriendsItemViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>

    sealed interface Content
    class DetailContent(val nickname: String): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FriendsItemViewHolder(parent, itemDelegate)
    override fun getItemCount() = adapterDelegate.getItemCount()
    override fun onBindViewHolder(holder: FriendsItemViewHolder, position: Int) = holder.bind(adapterDelegate.getItem(position) as DetailContent)
}