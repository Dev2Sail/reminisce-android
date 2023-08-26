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
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        val categories: List<CategoryVO>

        fun onCategoryClick(category: CategoryVO)
    }

    constructor(parent: ViewGroup, delegate: Delegate) : this(
        viewBinding = CardCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(category: CategoryVO) {
        if (category.title == "Default") {
            viewBinding.homeCategoryTitle.text = viewBinding.root.context.getText(R.string.category_view_holder_title)
        } else {
            viewBinding.homeCategoryTitle.text = category.title
        }
//        viewBinding.homeCategoryBody.text = delegate.getCount(category.id).toString()
        viewBinding.homeCategoryAction1.setOnClickListener {
            delegate.onCategoryClick(category)
        }
        viewBinding.root.setOnLongClickListener {
            DeleteCategoryDialog(it.context, category.id)
            false
        }
    }
}