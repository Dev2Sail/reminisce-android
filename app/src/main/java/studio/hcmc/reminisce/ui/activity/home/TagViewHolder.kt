package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.chip.Chip
import studio.hcmc.reminisce.databinding.CardHomeTagBinding
import studio.hcmc.reminisce.databinding.ChipTagBinding
import studio.hcmc.reminisce.vo.tag.TagVO

class TagViewHolder(
    private val viewBinding: CardHomeTagBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(tagId: Int)
        fun onItemLongClick(tagId: Int, tagIndex: Int, position: Int)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardHomeTagBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: HomeAdapter.TagContent) {
        val tags = content.tags
        if (!tags.isNullOrEmpty()) {
            viewBinding.homeTagChips.removeAllViews()
            for ((index, tag) in tags.withIndex()) {
                val chip = prepareChip(index, tag)
                viewBinding.homeTagChips.addView(chip)
            }
        } else { viewBinding.root.isGone = true }
    }

    private fun prepareChip(index: Int, tag: TagVO): Chip {
        val inflater = LayoutInflater.from(viewBinding.root.context)
        val chipToAttach = ChipTagBinding.inflate(inflater, viewBinding.homeTagChips, false).root
        chipToAttach.text = tag.body
        chipToAttach.isCheckable = false
        chipToAttach.isSelected = true
        chipToAttach.setOnClickListener {
            delegate.onItemClick(tag.id)
        }
        chipToAttach.setOnLongClickListener {
            delegate.onItemLongClick(tag.id, index, bindingAdapterPosition)

            false
        }

        return chipToAttach
    }
}