package studio.hcmc.reminisce.ui.activity.friend_tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardDateSeparatorBinding

class FriendTagDateViewHolder(
    private val viewBinding: CardDateSeparatorBinding
): ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup): this(
        viewBinding = CardDateSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(content: FriendTagAdapter.DateContent) {
        viewBinding.cardDateSeparator.text = content.body
    }
}