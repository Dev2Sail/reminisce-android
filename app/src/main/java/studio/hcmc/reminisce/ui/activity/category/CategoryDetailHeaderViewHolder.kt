package studio.hcmc.reminisce.ui.activity.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCategoryDetailHeaderBinding

class CategoryDetailHeaderViewHolder (
    private val viewBinding: CardCategoryDetailHeaderBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick()
        fun onTitleEditClick()
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardCategoryDetailHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: CategoryDetailAdapter.HeaderContent) {
        viewBinding.apply {
            cardCategoryDetailHeaderTitle.text = when(content.title) {
                "Default" -> viewBinding.root.context.getString(R.string.category_view_holder_title)
                "new" -> viewBinding.root.context.getString(R.string.add_category_body)
                else -> content.title
            }
            cardCategoryDetailHeaderEditIcon.setOnClickListener {
                delegate.onTitleEditClick()
            }
            cardCategoryDetailHeaderAction1.setOnClickListener {
                delegate.onItemClick()
            }
        }
    }
}