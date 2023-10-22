package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
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

        fun getUser(userId: Int): UserVO
        fun onItemClick(friend: FriendVO)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardHomeTagFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: HomeAdapter.FriendContent) {
        val friends = content.friends
        if (!friends.isNullOrEmpty()) {
            viewBinding.homePersonTagChipGroup.removeAllViews()
            for (friend in friends) {
                viewBinding.homePersonTagChipGroup.addView(LayoutInflater.from(viewBinding.root.context)
                    .let { ChipTagBinding.inflate(it, viewBinding.homePersonTagChipGroup, false) }
                    .root
                    .apply {
                        text = friend.nickname ?: delegate.getUser(friend.opponentId).nickname
                        isCheckable = false
                        isSelected = true
                        setOnClickListener { delegate.onItemClick(friend) }
                    }
                )
            }
        } else {
            viewBinding.root.isGone = true
        }
    }
}
// userId 30, friend (31(내가너무바빠), 32(null), 33(null), 42(아유졸려)