package studio.hcmc.reminisce.ui.activity.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardFriendsItemBinding
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class FriendsItemViewHolder(
    private val viewBinding: CardFriendsItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(opponentId: Int, friend: FriendVO)
        fun onItemLongClick(opponentId: Int)
        fun getUser(userId: Int): UserVO
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardFriendsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(friend: FriendVO) {
        viewBinding.apply {
            friendsItemTitle.text = friend.nickname ?: delegate.getUser(friend.opponentId).nickname
            friendsItemIcon.setOnClickListener {
                delegate.onItemClick(friend.opponentId, friend)
            }
            root.setOnLongClickListener {
                delegate.onItemLongClick(friend.opponentId)

                false
            }
        }
    }
}


