package studio.hcmc.reminisce.ui.activity.category.editable

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class CategoryEditableDetailAdapter(
    private val adapterDelegate: Delegate,
    private val summaryDelegate: ItemViewHolder.Delegate
): Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>

    sealed interface Content

    data class DetailContent(
        val location: LocationVO,
        val tags: List<TagVO>? = null,
        val friends: List<FriendVO>? = null
    ): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(parent, summaryDelegate)

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when(holder) {
        is ItemViewHolder -> holder.bind(adapterDelegate.getItem(position) as DetailContent)
        else -> unknownViewHolder(holder, position)
    }
}