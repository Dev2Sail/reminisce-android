package studio.hcmc.reminisce.ui.activity.writer.options

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardSelectFriendItemBinding

class WriteOptionFriendItemViewHolder(
    private val viewBinding: CardSelectFriendItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(opponentId: Int): Boolean
        fun isChecked(opponentId: Int): Boolean
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardSelectFriendItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: WriteOptionsFriendAdapter.DetailContent) {
        val (opponentId, nickname) = content
        viewBinding.writeSelectFriendTitle.text = nickname
        viewBinding.writeSelectFriendIcon.isVisible = delegate.isChecked(opponentId)
        viewBinding.root.setOnClickListener {
            viewBinding.writeSelectFriendIcon.isVisible = delegate.onItemClick(opponentId)
        }
    }
}
