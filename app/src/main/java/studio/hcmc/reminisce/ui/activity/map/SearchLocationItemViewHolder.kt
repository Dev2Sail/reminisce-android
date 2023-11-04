package studio.hcmc.reminisce.ui.activity.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardSearchLocationItemBinding

class SearchLocationItemViewHolder(
    private val viewBinding: CardSearchLocationItemBinding,
    private val delegate: Delegate
):ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onClick(placeId: String, placeName: String, roadAddress: String)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardSearchLocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: SearchLocationAdapter.PlaceContent) {
        val (placeId, placeName, placeCategory, placeRoadAddress) = content
        viewBinding.searchLocationPlace.text = placeName
        viewBinding.searchLocationCategory.text = placeCategory ?: ""
        viewBinding.searchLocationRoadAddress.text = placeRoadAddress
        viewBinding.root.setOnClickListener { delegate.onClick(placeId, placeName, placeRoadAddress) }
    }
}