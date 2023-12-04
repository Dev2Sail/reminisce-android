package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.chip.Chip
import studio.hcmc.reminisce.databinding.CardHomeTagFriendBinding
import studio.hcmc.reminisce.databinding.ChipTagBinding
import studio.hcmc.reminisce.vo.friend.FriendVO

class FriendTagViewHolder(
    private val viewBinding: CardHomeTagFriendBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(opponentId: Int)
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
            for ((index, friend) in friends.withIndex()) {
                val chip = prepareChip(index, friend)
                viewBinding.homePersonTagChips.addView(chip)
            }
        } else { viewBinding.root.isGone = true }
    }

    private fun prepareChip(index: Int, friend: FriendVO): Chip {
        val inflater = LayoutInflater.from(viewBinding.root.context)
        val chipToAttach = ChipTagBinding.inflate(inflater, viewBinding.homePersonTagChips, false).root
        chipToAttach.text = friend.nickname
        chipToAttach.isCheckable = false
        chipToAttach.isSelected = true
        chipToAttach.setOnClickListener {
            delegate.onItemClick(friend.opponentId)
        }
        chipToAttach.setOnLongClickListener {
            delegate.onItemLongClick(friend.opponentId, index, bindingAdapterPosition)

            false
        }

        return chipToAttach
    }
}