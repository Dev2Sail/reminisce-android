package studio.hcmc.reminisce.ui.activity.writer.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewType
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class WriteDetailAdapter(
    private val adapterDelegate: Delegate
): Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>

    sealed interface Content

    class DetailContent(val location: LocationVO): Content
    data class OptionsContent(
        val category: CategoryVO,
        val tags: List<TagVO>? = null,
        val friends: List<FriendVO>? = null
    ): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        0 -> WriteDetailContentViewHolder(parent)
        1 -> WriteDetailOptionsViewHolder(parent)
        else -> unknownViewType(viewType)
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when(holder) {
        is WriteDetailContentViewHolder -> holder.bind(adapterDelegate.getItem(position) as DetailContent)
        is WriteDetailOptionsViewHolder -> holder.bind(adapterDelegate.getItem(position) as OptionsContent)
        else -> unknownViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int) = when(adapterDelegate.getItem(position)) {
        is DetailContent -> 0
        is OptionsContent -> 1
    }
}