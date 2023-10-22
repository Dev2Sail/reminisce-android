package studio.hcmc.reminisce.ui.activity.writer.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class WriteDetailAdapter(
    private val adapterDelegate: Delegate

): Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>
    sealed interface Content

    class DetailContent(val location: LocationVO): Content
    class TagContent(val tags: List<TagVO>? = null): Content
    class FriendContent(val friends: List<FriendVO>? = null): Content

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}