package studio.hcmc.reminisce.ui.activity.friend_tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCommonDetailHeaderBinding

class FriendTagHeaderViewHolder(
    private val viewBinding: CardCommonDetailHeaderBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onEditClick()
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardCommonDetailHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: FriendTagAdapter.HeaderContent) {
        viewBinding.apply {
            cardCommonDetailHeaderTitle.text = content.title
            cardCommonDetailHeaderAction1.text = viewBinding.root.context.getString(R.string.header_action)
            cardCommonDetailHeaderAction1.setOnClickListener { delegate.onEditClick() }
        }
    }
}