package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCategoryItemBinding
import studio.hcmc.reminisce.vo.category.CategoryVO

class CategoryViewHolder(
    private val viewBinding: CardCategoryItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(category: CategoryVO, position: Int)
        fun onItemLongClick(categoryId: Int, position: Int)
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: HomeAdapter.CategoryContent) {
        val (category, count) = content

        viewBinding.homeCategoryTitle.text = when (category.title) {
            "Default" -> viewBinding.root.context.getText(R.string.category_view_holder_title)
            "new" -> viewBinding.root.context.getText(R.string.add_category_body)
            else -> category.title
        }

        viewBinding.homeCategoryBody.text = count.toString()
        viewBinding.homeCategoryAction1.setOnClickListener {
            delegate.onItemClick(category, bindingAdapterPosition)
        }
        viewBinding.root.setOnLongClickListener {
            delegate.onItemLongClick(category.id, bindingAdapterPosition - 1)

            false
        }
    }
}