package studio.hcmc.reminisce.ui.activity.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardCommonUneditableHeaderBinding
import studio.hcmc.reminisce.vo.tag.TagVO

class TagDetailHeaderViewHolder(
    private val viewBinding: CardCommonUneditableHeaderBinding
): ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup) : this(
        viewBinding = CardCommonUneditableHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(tag: TagVO) {
        viewBinding.cardCommonUneditableHeaderTitle.text = tag.body
    }
}