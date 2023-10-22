package studio.hcmc.reminisce.ui.activity.friend_tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardCommonHeaderBinding

class FriendTagHeaderViewHolder(
    private val viewBinding: CardCommonHeaderBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onEditClick(title: String)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardCommonHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: FriendTagAdapter.HeaderContent) {
        viewBinding.commonHeaderTitle.text = content.title
        viewBinding.commonHeaderAction1.setOnClickListener {
            delegate.onEditClick(content.title)
        }
    }
}