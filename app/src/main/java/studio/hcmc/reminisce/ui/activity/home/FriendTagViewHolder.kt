package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardHomeTagFriendBinding
import studio.hcmc.reminisce.databinding.ChipTagBinding
import studio.hcmc.reminisce.vo.location_friend.LocationFriendVO

class FriendTagViewHolder(
    private val viewBinding: CardHomeTagFriendBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        val friendTags: List<LocationFriendVO>

        fun onTagClick(friendTag: LocationFriendVO)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardHomeTagFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind() {
        viewBinding.homePersonTagChipGroup.removeAllViews()
//        for (personTag in delegate.friendTags) {
//            viewBinding.homePersonTagChipGroup.addView(ChipConfig(viewBinding.root.context) {
//
//                text = "${personTag.opponentId}"
//                isCheckable = false
//                setOnClickListener {
//                    delegate.onTagClick(personTag)
//                }
//            })
//        }
        for (friendTag in delegate.friendTags) {
            viewBinding.homePersonTagChipGroup.addView(LayoutInflater.from(viewBinding.root.context)
                .let { ChipTagBinding.inflate(it, viewBinding.homePersonTagChipGroup, false) }
                .root
                .apply {
                    text = "${friendTag.opponentId}"
                    isCheckable = false
                    isSelected = true
                    setOnClickListener { delegate.onTagClick(friendTag) }
                }
            )
        }
    }
}