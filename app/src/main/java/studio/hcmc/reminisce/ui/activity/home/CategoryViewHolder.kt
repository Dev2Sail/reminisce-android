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
        fun onItemClick(category: CategoryVO)
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

        viewBinding.apply {
            homeCategoryBody.text = count.toString()
            homeCategoryAction1.setOnClickListener { delegate.onItemClick(category) }
            root.setOnLongClickListener {
                DeleteCategoryDialog(it.context, category.id)

                false
            }
        }
    }
}