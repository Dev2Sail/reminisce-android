package studio.hcmc.reminisce.ui.activity.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardDateSeparatorBinding

class TagDateDividerViewHolder(
    private val viewBinding: CardDateSeparatorBinding
): ViewHolder(viewBinding.root) {

    constructor(parent: ViewGroup): this(
        viewBinding = CardDateSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(content: TagDetailAdapter.TagContents) {
        val dateContent = (content as? TagDetailAdapter.TagDetailDateDividerContent)?.body ?: return
        viewBinding.cardDateSeparator.text = dateContent
    }
}

