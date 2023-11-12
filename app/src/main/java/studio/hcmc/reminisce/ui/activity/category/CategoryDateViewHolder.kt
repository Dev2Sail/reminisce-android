package studio.hcmc.reminisce.ui.activity.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardDateSeparatorBinding

class CategoryDateViewHolder (
    private val viewBinding: CardDateSeparatorBinding
) : ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup): this(
        viewBinding = CardDateSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(content: CategoryDetailAdapter.DateContent) {
        if (content.body.isNullOrEmpty()) {
            viewBinding.cardDateSeparator.isGone = true
        } else {
            viewBinding.cardDateSeparator.isVisible = true
            viewBinding.cardDateSeparator.text = content.body
        }
    }
}