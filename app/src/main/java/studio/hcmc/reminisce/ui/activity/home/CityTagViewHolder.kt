package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardHomeTagCityBinding
import studio.hcmc.reminisce.databinding.ChipTagBinding

class CityTagViewHolder(
    private val viewBinding: CardHomeTagCityBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        val cityTags: List<String>

        fun onTagClick(locationTag: String)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardHomeTagCityBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind() {
        viewBinding.homeLocationTagChipGroup.removeAllViews()
//        for (cityTag in delegate.cityTags) {
//            viewBinding.homeLocationTagChipGroup.addView(ChipConfig(viewBinding.root.context) {
//                text = cityTag
//                isCheckable = false
//                setOnClickListener {
//                    delegate.onTagClick(cityTag)
//                }
//            })
//        }
        for (cityTag in delegate.cityTags) {
            viewBinding.homeLocationTagChipGroup.addView(LayoutInflater.from(viewBinding.root.context)
                .let { ChipTagBinding.inflate(it, viewBinding.homeLocationTagChipGroup, false) }
                .root
                .apply {
                    text = cityTag
                    isCheckable = false
                    isSelected = true
                    setOnClickListener { delegate.onTagClick(cityTag) }
                }
            )
        }
    }
}
