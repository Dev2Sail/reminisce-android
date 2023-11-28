package studio.hcmc.reminisce.ui.activity.report

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewType
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class YearAgoTodayAdapter(
    private val adapterDelegate: Delegate,
    private val itemDelegate: YearAgoItemViewHolder.Delegate
): Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>
    sealed interface Content

    class DateContent(val body: String? = null): Content

    data class DetailContent(
        val location: LocationVO,
        val tags: List<TagVO>? = null,
        val friends: List<FriendVO>? = null
    ): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        0 -> YearAgoTodayDateViewHolder(parent)
        1 -> YearAgoItemViewHolder(parent, itemDelegate)
        else -> unknownViewType(viewType)
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when(holder) {
        is YearAgoTodayDateViewHolder -> holder.bind(adapterDelegate.getItem(position) as DateContent)
        is YearAgoItemViewHolder -> holder.bind(adapterDelegate.getItem(position) as DetailContent)
        else -> unknownViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int) = when(adapterDelegate.getItem(position)) {
        is DateContent -> 0
        is DetailContent -> 1
    }
}