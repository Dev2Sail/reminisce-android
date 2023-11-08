package studio.hcmc.reminisce.ui.activity.writer.options

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate

class WriteOptionsFriendAdapter(
    private val adapterDelegate: Delegate,
    private val itemDelegate: WriteOptionFriendItemViewHolder.Delegate
): Adapter<WriteOptionFriendItemViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>

    sealed interface Content
    data class DetailContent(val opponentId: Int, val nickname: String): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WriteOptionFriendItemViewHolder(parent, itemDelegate)
    override fun getItemCount() = adapterDelegate.getItemCount()
    override fun onBindViewHolder(holder: WriteOptionFriendItemViewHolder, position: Int) = holder.bind(adapterDelegate.getItem(position) as DetailContent)
}
