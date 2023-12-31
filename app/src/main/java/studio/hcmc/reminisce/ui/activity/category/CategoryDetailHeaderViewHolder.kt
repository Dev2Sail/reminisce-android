package studio.hcmc.reminisce.ui.activity.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCategoryDetailHeaderBinding

class CategoryDetailHeaderViewHolder (
    private val viewBinding: CardCategoryDetailHeaderBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        val title: String
        fun onClick()
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardCategoryDetailHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind() {
        if (delegate.title == "Default") {
            viewBinding.cardCategoryDetailHeaderTitle.text = viewBinding.root.context.getText(R.string.category_view_holder_title)
        } else {
            viewBinding.cardCategoryDetailHeaderTitle.text = delegate.title
        }

        viewBinding.cardCategoryDetailHeaderAction1.setOnClickListener {
            delegate.onClick()
        }
    }
}