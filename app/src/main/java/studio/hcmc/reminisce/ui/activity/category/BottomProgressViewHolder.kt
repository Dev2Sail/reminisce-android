package studio.hcmc.reminisce.ui.activity.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardBottomProgressBinding

class BottomProgressViewHolder(private val viewBinding: CardBottomProgressBinding): ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup): this(CardBottomProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    var isVisible: Boolean
        get() = viewBinding.root.isVisible
        set(value) { viewBinding.root.isVisible = value }

    init {
        setIsRecyclable(false)
    }
}