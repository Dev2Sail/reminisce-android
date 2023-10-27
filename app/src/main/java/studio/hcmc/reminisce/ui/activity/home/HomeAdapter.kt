package studio.hcmc.reminisce.ui.activity.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.ui.view.unknownViewHolder
import studio.hcmc.reminisce.ui.view.unknownViewType
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.tag.TagVO

class HomeAdapter(
    private val adapterDelegate: Delegate,
    private val headerDelegate: HeaderViewHolder.Delegate,
    private val categoryDelegate: CategoryViewHolder.Delegate,
    private val tagDelegate: TagViewHolder.Delegate,
    private val friendTagDelegate: FriendTagViewHolder.Delegate
//    private val cityTagDelegate: CityTagViewHolder.Delegate
) : Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>

    sealed interface Content

    class HeaderContent(private val o: Any? = null): Content
    data class CategoryContent(
        val category: CategoryVO,
        val count: Int
    ): Content
    class TagContent(val tags: List<TagVO>? = null): Content
    class FriendContent(val friends: List<FriendVO>? = null): Content
    //    class CityContent(val body: String? = null): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
        0 -> HeaderViewHolder(parent, headerDelegate)
        1 -> CategoryViewHolder(parent, categoryDelegate)
        2 -> TagViewHolder(parent, tagDelegate)
        3 -> FriendTagViewHolder(parent, friendTagDelegate)
        else -> unknownViewType(viewType)
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = when(holder) {
        is HeaderViewHolder -> holder.bind()
        is CategoryViewHolder -> holder.bind(adapterDelegate.getItem(position) as CategoryContent)
        is TagViewHolder -> holder.bind(adapterDelegate.getItem(position) as TagContent)
        is FriendTagViewHolder -> holder.bind(adapterDelegate.getItem(position) as FriendContent)
        else -> unknownViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int) = when(adapterDelegate.getItem(position)) {
        is HeaderContent -> 0
        is CategoryContent -> 1
        is TagContent -> 2
        is FriendContent -> 3
    }
}