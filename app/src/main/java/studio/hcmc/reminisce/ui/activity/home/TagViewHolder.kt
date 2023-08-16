package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardHomeTagBinding
import studio.hcmc.reminisce.databinding.ChipTagBinding
import studio.hcmc.reminisce.vo.tag.TagVO

class TagViewHolder(
    private val viewBinding: CardHomeTagBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        val tags: List<TagVO>

        fun onTagClick(tag: TagVO)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardHomeTagBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind() {
        viewBinding.homeTagChips.removeAllViews()
        for (tag in delegate.tags) {
            viewBinding.homeTagChips.addView(LayoutInflater.from(viewBinding.root.context)
                .let { ChipTagBinding.inflate(it, viewBinding.homeTagChips, false) }
                .root
                .apply {
                    text = tag.body
                    isCheckable = false
                    isSelected = true
                    setOnClickListener { delegate.onTagClick(tag) }
                }
            )
        }
    }
}