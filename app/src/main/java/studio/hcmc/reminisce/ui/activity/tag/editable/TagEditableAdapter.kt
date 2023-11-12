package studio.hcmc.reminisce.ui.activity.tag.editable

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class TagEditableAdapter(
    private val adapterDelegate: Delegate,
    private val summaryDelegate: SummaryViewHolder.Delegate
): Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>

    sealed interface Content

    data class DetailContent(
        val location: LocationVO,
        val tags: List<TagVO>,
        val friends: List<FriendVO>? = null
    ): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SummaryViewHolder(parent, summaryDelegate)

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when(holder) {
        is SummaryViewHolder -> holder.bind(adapterDelegate.getItem(position) as DetailContent)
        else -> unknownViewHolder(holder, position)
    }
}