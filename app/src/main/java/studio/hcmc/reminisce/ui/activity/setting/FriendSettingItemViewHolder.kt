package studio.hcmc.reminisce.ui.activity.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.LayoutSettingFriendItemBinding
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class FriendSettingItemViewHolder(
    private val viewBinding: LayoutSettingFriendItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate: SingleTypeAdapterDelegate<FriendVO> {
        val friends: List<FriendVO>
        val users: HashMap<Int /* userId */, UserVO>
        fun onItemClick(opponentId: Int, friend: FriendVO)
        fun onItemLongClick(opponentId: Int)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = LayoutSettingFriendItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    private fun getFriend(userId: Int): UserVO { return delegate.users[userId]!!}

    fun bind(friend: FriendVO) {
        viewBinding.apply {
            settingFriendTitle.text = friend.nickname ?: getFriend(friend.opponentId).nickname
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


