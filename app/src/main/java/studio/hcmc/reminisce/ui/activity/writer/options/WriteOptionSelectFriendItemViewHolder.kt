package studio.hcmc.reminisce.ui.activity.writer.options

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardSelectFriendItemBinding
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class WriteOptionSelectFriendItemViewHolder(
    private val viewBinding: CardSelectFriendItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate: SingleTypeAdapterDelegate<FriendVO> {
        val friends: List<FriendVO>
        val users: HashMap<Int, UserVO>
        fun onItemClick(opponentId: Int): Boolean
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardSelectFriendItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    private fun getFriend(userId: Int): UserVO { return delegate.users[userId]!!}

    fun bind(friend: FriendVO) {
        viewBinding.writeSelectFriendTitle.text = friend.nickname ?: getFriend(friend.opponentId).nickname

        viewBinding.apply {
            root.setOnClickListener {
                writeSelectFriendIcon.isVisible = delegate.onItemClick(friend.opponentId)
            }
        }
    }
}
