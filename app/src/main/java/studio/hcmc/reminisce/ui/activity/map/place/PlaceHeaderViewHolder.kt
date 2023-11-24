package studio.hcmc.reminisce.ui.activity.map.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardCommonDetailHeaderBinding

class PlaceHeaderViewHolder(
    private val viewBinding: CardCommonDetailHeaderBinding
): ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup):this (
        viewBinding = CardCommonDetailHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(content: PlaceAdapter.HeaderContent) {
        viewBinding.cardCommonDetailHeaderTitle.text = content.title
        viewBinding.cardCommonDetailHeaderAction1.isVisible = false
    }
}