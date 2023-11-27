package studio.hcmc.reminisce.ui.activity.category

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
    interface Delegate: SingleTypeAdapterDelegate<Content> {
        fun hasMoreContents(): Boolean
        fun getMoreContents()
    }

    sealed interface Content
    class HeaderContent(val title: String): Content
    class DateContent(val body: String? = null): Content
    data class DetailContent(
        val location: LocationVO,
        val tags: List<TagVO>? = null,
        val friends: List<FriendVO>? = null
    ): Content

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // 최하단에 도착하지 않았다면
            if (!recyclerView.canScrollVertically(1)) {
                adapterDelegate.getMoreContents()
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        recyclerView.removeOnScrollListener(onScrollListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        0 -> CategoryDetailHeaderViewHolder(parent, headerDelegate)
        1 -> CategoryDateViewHolder(parent)
        2 -> CategoryDetailSummaryViewHolder(parent, summaryDelegate)
        3 -> BottomProgressViewHolder(parent)
        else -> unknownViewType(viewType)
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when(holder) {
        is CategoryDetailHeaderViewHolder -> holder.bind(adapterDelegate.getItem(position) as HeaderContent)
        is CategoryDateViewHolder -> holder.bind(adapterDelegate.getItem(position) as DateContent)
        is CategoryDetailSummaryViewHolder -> holder.bind(adapterDelegate.getItem(position) as DetailContent)
        is BottomProgressViewHolder -> holder.isVisible = adapterDelegate.hasMoreContents()
        else -> unknownViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int) = when(adapterDelegate.getItem(position)) {
        is HeaderContent -> 0
        is DateContent -> 1
        is DetailContent -> 2
    }
}
