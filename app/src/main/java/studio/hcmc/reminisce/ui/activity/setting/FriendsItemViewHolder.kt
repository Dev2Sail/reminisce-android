package studio.hcmc.reminisce.ui.activity.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardFriendsItemBinding
import studio.hcmc.reminisce.vo.user.UserVO

class FriendsItemViewHolder(
    private val viewBinding: CardFriendsItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
//        fun onItemClick(opponentId: Int, savedNickname: String?, position: Int)
        fun onItemLongClick(opponentId: Int, position: Int)
        fun getUser(userId: Int): UserVO

        fun onTestClick(nickname: String, position: Int)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardFriendsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: FriendsAdapter.DetailContent) {
        viewBinding.apply {
            friendsItemTitle.text = content.nickname
            friendsItemIcon.setOnClickListener {
                delegate.onTestClick(content.nickname, bindingAdapterPosition)
//                delegate.onItemClick()

            }
            root.setOnLongClickListener {
//                delegate.onItemLongClick(friend!!.opponentId, bindingAdapterPosition)

                false
            }
        }
    }
}


