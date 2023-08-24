package studio.hcmc.reminisce.ui.activity.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewType

class HomeAdapter(
    private val headerDelegate: HeaderViewHolder.Delegate,
    private val categoryDelegate: CategoryViewHolder.Delegate,
    private val friendTagDelegate: FriendTagViewHolder.Delegate,
    private val cityTagDelegate: CityTagViewHolder.Delegate,
    private val tagDelegate: TagViewHolder.Delegate
) : Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder = when(viewType) {
        0 -> HeaderViewHolder(parent, headerDelegate)
        1 -> CategoryViewHolder(parent, categoryDelegate)
        2 -> FriendTagViewHolder(parent, friendTagDelegate)
        3 -> CityTagViewHolder(parent, cityTagDelegate)
        4 -> TagViewHolder(parent, tagDelegate)
        else -> unknownViewType(viewType)
    }

    override fun getItemCount() = categoryDelegate.categories.size + 4

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when (holder) {
        is HeaderViewHolder -> holder.bind()
        is CategoryViewHolder -> holder.bind(categoryDelegate.categories[position - 1])
        is TagViewHolder -> holder.bind()
        is FriendTagViewHolder -> holder.bind()
        is CityTagViewHolder -> holder.bind()
        else -> unknownViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        val categorySize = categoryDelegate.categories.size
        return when (position) {
            0 -> 0
            in 1..categorySize -> 1
            categorySize + 1 -> 2
            categorySize + 2 -> 3
            categorySize + 3 -> 4
            else -> throw AssertionError()
        }
    }
}