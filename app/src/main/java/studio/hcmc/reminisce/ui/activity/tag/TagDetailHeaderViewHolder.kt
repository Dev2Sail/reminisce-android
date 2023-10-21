package studio.hcmc.reminisce.ui.activity.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardCommonUneditableHeaderBinding

class TagDetailHeaderViewHolder(
    private val viewBinding: CardCommonUneditableHeaderBinding,
    private val delegate: Delegate

): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onEditClick(title: String)
    }
    constructor(parent: ViewGroup, delegate: Delegate) : this(
        viewBinding = CardCommonUneditableHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: TagDetailAdapter.TagDetailHeaderContent) {
        viewBinding.cardCommonUneditableHeaderTitle.text = content.title
        viewBinding.cardCommonUneditableHeaderAction1.setOnClickListener {
            delegate.onEditClick(content.title)
        }
    }
}