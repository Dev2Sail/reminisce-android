package studio.hcmc.reminisce.ui.activity.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardFriendsItemBinding
import studio.hcmc.reminisce.vo.friend.FriendVO

class FriendsItemViewHolder(
    private val viewBinding: CardFriendsItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(friend: FriendVO, position: Int)
        fun onItemLongClick(opponentId: Int, position: Int)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardFriendsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: FriendsAdapter.DetailContent) {
        val friend = content.friend
        viewBinding.apply {
            friendsItemTitle.text = friend.nickname
            friendsItemIcon.setOnClickListener {
                delegate.onItemClick(friend, bindingAdapterPosition)
            }
            root.setOnLongClickListener {
                delegate.onItemLongClick(friend.opponentId, bindingAdapterPosition)

                false
            }
        }
    }
}


