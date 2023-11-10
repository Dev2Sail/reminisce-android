package studio.hcmc.reminisce.ui.activity.category

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewType
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class CategoryDetailAdapter(
    private val adapterDelegate: Delegate,
    private val headerDelegate: CategoryDetailHeaderViewHolder.Delegate,
    private val summaryDelegate: CategoryDetailSummaryViewHolder.Delegate
) : Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>

    sealed interface Content
    class HeaderContent(val title: String): Content
    class DateContent(val body: String? = null): Content
    data class DetailContent(
        val location: LocationVO? = null,
        val tags: List<TagVO>? = null,
        val friends: List<FriendVO>? = null
//        val count: String
    ): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        0 -> CategoryDetailHeaderViewHolder(parent, headerDelegate)
        1 -> CategoryDateDividerViewHolder(parent)
        2 -> CategoryDetailSummaryViewHolder(parent, summaryDelegate)
        else -> unknownViewType(viewType)
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when(holder) {
        is CategoryDetailHeaderViewHolder -> holder.bind(adapterDelegate.getItem(position) as HeaderContent)
        is CategoryDateDividerViewHolder -> holder.bind(adapterDelegate.getItem(position) as DateContent)
        is CategoryDetailSummaryViewHolder -> holder.bind(adapterDelegate.getItem(position) as DetailContent)
        else -> unknownViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int) = when(adapterDelegate.getItem(position)) {
        is HeaderContent -> 0
        is DateContent -> 1
        is DetailContent -> 2
    }
}
