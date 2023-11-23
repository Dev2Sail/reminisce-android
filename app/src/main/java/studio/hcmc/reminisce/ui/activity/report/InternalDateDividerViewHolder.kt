package studio.hcmc.reminisce.ui.activity.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardDateSeparatorBinding

class InternalDateDividerViewHolder(
    private val viewBinding: CardDateSeparatorBinding
): ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup): this(
        viewBinding = CardDateSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(content: InternalDetailAdapter.DateContent) {
        viewBinding.cardDateSeparator.text = content.body
    }
}