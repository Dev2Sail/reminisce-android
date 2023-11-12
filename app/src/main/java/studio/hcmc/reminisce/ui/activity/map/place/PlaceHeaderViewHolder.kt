package studio.hcmc.reminisce.ui.activity.map.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardCommonDetailHeaderBinding

class PlaceHeaderViewHolder(
    private val viewBinding: CardCommonDetailHeaderBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onEditClick()
    }

    constructor(parent: ViewGroup, delegate: Delegate):this (
        viewBinding = CardCommonDetailHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: PlaceAdapter.HeaderContent) {
        viewBinding.cardCommonDetailHeaderTitle.text = content.title
        viewBinding.cardCommonDetailHeaderAction1.setOnClickListener { delegate.onEditClick() }
    }
}