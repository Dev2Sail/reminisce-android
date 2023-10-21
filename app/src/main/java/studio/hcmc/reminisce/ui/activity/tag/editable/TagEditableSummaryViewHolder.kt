package studio.hcmc.reminisce.ui.activity.tag.editable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardTagEditableSummaryBinding

class TagEditableSummaryViewHolder(
    private val viewBinding: CardTagEditableSummaryBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {

    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardTagEditableSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind() {

    }
}