package studio.hcmc.reminisce.ui.activity.map

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder

class SearchLocationAdapter(
    private val adapterDelegate: Delegate,
    private val itemDelegate: SearchLocationItemViewHolder.Delegate
):Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>

    sealed interface Content

    data class PlaceContent(
        val id: String, // place.id
        val name: String, // 장소 (place_name)
        val category: String? = null, // 카테고리 (category_group_name)
        val roadAddress: String? // 도로명 주소 (road_address_name)
    ): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchLocationItemViewHolder(parent, itemDelegate)
    override fun getItemCount() = adapterDelegate.getItemCount()
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when(holder) {
        is SearchLocationItemViewHolder -> holder.bind(adapterDelegate.getItem(position) as PlaceContent)
        else -> unknownViewHolder(holder, position)
    }
}