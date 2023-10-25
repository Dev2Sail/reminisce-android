package studio.hcmc.reminisce.ui.activity.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardFriendsItemBinding
import studio.hcmc.reminisce.vo.user.UserVO

class FriendsItemViewHolder(
    private val viewBinding: CardFriendsItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(opponentId: Int, savedNickname: String?, position: Int)
        fun onItemLongClick(opponentId: Int, position: Int)
        fun getUser(userId: Int): UserVO
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardFriendsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: FriendsAdapter.DetailContent) {
        val friend = content.friend
        if (content.friend != null) {
            viewBinding.root.isVisible = true
            viewBinding.apply {
                friendsItemTitle.text = friend!!.nickname ?: delegate.getUser(friend.opponentId).nickname
                friendsItemIcon.setOnClickListener {
                    delegate.onItemClick(friend.opponentId, friend.nickname, bindingAdapterPosition)
                }
            }
            viewBinding.root.setOnLongClickListener {
                delegate.onItemLongClick(friend!!.opponentId, bindingAdapterPosition)

                false
            }
        } else {
            viewBinding.root.isGone = true
        }
    }
}


