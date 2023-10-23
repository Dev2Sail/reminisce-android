package studio.hcmc.reminisce.ui.activity.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardSettingFriendItemBinding
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class FriendListViewHolder(
    private val viewBinding: CardSettingFriendItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(opponentId: Int, friend: FriendVO)
        fun onItemLongClick(opponentId: Int)
        fun getUser(userId: Int): UserVO
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardSettingFriendItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(friend: FriendVO) {
        viewBinding.apply {
            settingFriendTitle.text = friend.nickname ?: delegate.getUser(friend.opponentId).nickname
            settingFriendItemIcon.setOnClickListener {
                delegate.onItemClick(friend.opponentId, friend)
            }
            root.setOnLongClickListener {
                delegate.onItemLongClick(friend.opponentId)

                false
            }
        }
    }
}


