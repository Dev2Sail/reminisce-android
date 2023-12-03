package studio.hcmc.reminisce.ui.activity.writer.options

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.activity.common.BottomProgressViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewType

class WriteOptionsFriendAdapter(
    private val adapterDelegate: Delegate,
    private val itemDelegate: WriteOptionFriendItemViewHolder.Delegate
): Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content> {
        fun hasMoreContents(): Boolean
        fun getMoreContents()
    }

    sealed interface Content
    data class DetailContent(val opponentId: Int, val nickname: String): Content
    object ProgressContent: Content

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

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
        0 -> WriteOptionFriendItemViewHolder(parent, itemDelegate)
        1 -> BottomProgressViewHolder(parent)
        else -> unknownViewType(viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when(holder) {
        is WriteOptionFriendItemViewHolder -> holder.bind(adapterDelegate.getItem(position) as DetailContent)
        is BottomProgressViewHolder -> holder.isVisible = adapterDelegate.hasMoreContents()
        else -> unknownViewHolder(holder, position)
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun getItemViewType(position: Int) = when(adapterDelegate.getItem(position)) {
        is DetailContent -> 0
        is ProgressContent -> 1
    }
}
