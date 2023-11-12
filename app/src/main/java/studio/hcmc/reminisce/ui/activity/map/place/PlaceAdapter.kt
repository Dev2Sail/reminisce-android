package studio.hcmc.reminisce.ui.activity.map.place

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewType
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class PlaceAdapter(
    private val adapterDelegate: Delegate,
    private val headerDelegate: PlaceHeaderViewHolder.Delegate,
    private val summaryDelegate: PlaceSummaryViewHolder.Delegate
): Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>

    sealed interface Content

    class HeaderContent(val title: String): Content
    class DateContent(val body: String? = null): Content
    data class DetailContent(
        val location: LocationVO,
        val tags: List<TagVO>,
        val friends: List<FriendVO>
    ): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        0 -> PlaceHeaderViewHolder(parent, headerDelegate)
        1 -> PlaceDateViewHolder(parent)
        2 -> PlaceSummaryViewHolder(parent, summaryDelegate)
        else -> unknownViewType(viewType)
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when (holder) {
        is PlaceHeaderViewHolder -> holder.bind(adapterDelegate.getItem(position) as HeaderContent)
        is PlaceDateViewHolder -> holder.bind(adapterDelegate.getItem(position) as DateContent)
        is PlaceSummaryViewHolder -> holder.bind(adapterDelegate.getItem(position) as DetailContent)
        else -> unknownViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int) = when (adapterDelegate.getItem(position)) {
        is HeaderContent -> 0
        is DateContent -> 1
        is DetailContent -> 2
    }
}