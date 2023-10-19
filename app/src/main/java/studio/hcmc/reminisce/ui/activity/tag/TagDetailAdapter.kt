package studio.hcmc.reminisce.ui.activity.tag

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.common.DateDividerViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewType
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class TagDetailAdapter(
    private val adapterDelegate: Delegate,
    private val summaryDelegate: TagDetailSummaryViewHolder.Delegate
): Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<TagContents> {
        fun getTag(): TagVO
        fun getLocation(): LocationVO
        fun getFriend(): FriendVO?
    }

    sealed interface TagContents

    class TagDetailHeaderContent(val title: String): TagContents
    class TagDetailDateDividerContent(val body: String): TagContents
    class TagDetailContent(val location: LocationVO): TagContents

    class TagDetailTagItemContent(val tag: TagVO): TagContents
    class TagDetailFriendItemContent(val friend: FriendVO): TagContents


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        0 -> TagDetailHeaderViewHolder(parent)
        1 -> DateDividerViewHolder(parent)
        2 -> TagDetailSummaryViewHolder(parent, summaryDelegate)
        else -> unknownViewType(viewType)
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when (holder) {
        is TagDetailHeaderViewHolder -> holder.bind(adapterDelegate.getTag())
        is DateDividerViewHolder -> holder.bind(adapterDelegate.getLocation().createdAt.toString().substring(0, 7))
        is TagDetailSummaryViewHolder -> holder.bind(adapterDelegate.getLocation())
        else -> unknownViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int) = when (adapterDelegate.getItem(position)) {
        is TagDetailHeaderContent -> 0
        is TagDetailDateDividerContent -> 1
        is TagDetailContent -> 2
        else -> unknownViewType(position)
    }
}