package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardHomeTagFriendBinding
import studio.hcmc.reminisce.databinding.ChipTagBinding
import studio.hcmc.reminisce.vo.user.UserVO

class FriendTagViewHolder(
    private val viewBinding: CardHomeTagFriendBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        fun getUser(userId: Int): UserVO
        fun onItemClick(opponentId: Int, nickname: String)
        fun onItemLongClick(opponentId: Int, friendIdx: Int, position: Int)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardHomeTagFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: HomeAdapter.FriendContent) {
        val friends = content.friends
        if (!friends.isNullOrEmpty()) {
            viewBinding.homePersonTagChips.removeAllViews()
            for (friend in friends.withIndex()) {
                val vo = friend.value
                viewBinding.homePersonTagChips.addView(LayoutInflater.from(viewBinding.root.context)
                    .let { ChipTagBinding.inflate(it, viewBinding.homePersonTagChips, false) }
                    .root
                    .apply {
                        text = vo.nickname ?: delegate.getUser(vo.opponentId).nickname
                        isCheckable = false
                        isSelected = true
                        setOnClickListener {
                            delegate.onItemClick(vo.opponentId, vo.nickname ?: delegate.getUser(vo.opponentId).nickname)
                        }
                        setOnLongClickListener {
                            delegate.onItemLongClick(friend.value.opponentId, friend.index, bindingAdapterPosition)

                            false
                        }
                    }
                )
            }
        } else {
            viewBinding.root.isGone = true
        }
    }
}