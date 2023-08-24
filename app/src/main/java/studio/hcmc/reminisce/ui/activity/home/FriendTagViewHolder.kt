package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardHomeTagFriendBinding
import studio.hcmc.reminisce.databinding.ChipTagBinding
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class FriendTagViewHolder(
    private val viewBinding: CardHomeTagFriendBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        val friends: List<FriendVO>

        fun getUser(userId: Int): UserVO

        fun onTagClick(friendTag: FriendVO)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardHomeTagFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind() {
        viewBinding.homePersonTagChipGroup.removeAllViews()
        for (friend in delegate.friends) {
            viewBinding.homePersonTagChipGroup.addView(LayoutInflater.from(viewBinding.root.context)
                .let { ChipTagBinding.inflate(it, viewBinding.homePersonTagChipGroup, false) }
                .root
                .apply {
                    text = friend.nickname ?: delegate.getUser(friend.opponentId).nickname
                    isCheckable = false
                    isSelected = true
                    setOnClickListener { delegate.onTagClick(friend) }
                }
            )
        }
    }
}