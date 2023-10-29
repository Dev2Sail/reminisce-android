package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardHomeTagBinding
import studio.hcmc.reminisce.databinding.ChipTagBinding
import studio.hcmc.reminisce.util.LocalLogger

class TagViewHolder(
    private val viewBinding: CardHomeTagBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(tagId: Int)

        fun onItemLongClick(tagId: Int, position: Int)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardHomeTagBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: HomeAdapter.TagContent) {
        val tags = content.tags
        when {
            !tags.isNullOrEmpty() -> {
                viewBinding.homeTagChips.removeAllViews()
                for (tag in tags) {
                    viewBinding.homeTagChips
                        .addView(LayoutInflater.from(viewBinding.root.context)
                        .let { ChipTagBinding.inflate(it, viewBinding.homeTagChips, false) }
                        .root
                        .apply {
                            text = tag.body
                            isCheckable = false
                            isSelected = true
                            setOnClickListener { delegate.onItemClick(tag.id) }
                            setOnLongClickListener {
                                delegate.onItemLongClick(tag.id, bindingAdapterPosition)
                                LocalLogger.v("position: $bindingAdapterPosition // $absoluteAdapterPosition // $verticalScrollbarPosition // $layoutPosition")

                                false
                            }
                        }
                    )
                }
            }
            else -> { viewBinding.root.isGone = true }
        }
    }
}