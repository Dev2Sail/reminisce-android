package studio.hcmc.reminisce.ui.activity.writer.options

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardWriteOptionsSelectCategoryItemBinding

class WriteOptionCategoryItemViewHolder(
    private val viewBinding: CardWriteOptionsSelectCategoryItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onItemClick(categoryId: Int): Boolean
    }

    constructor(parent: ViewGroup, delegate: Delegate) : this(
        viewBinding = CardWriteOptionsSelectCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind(content: WriteOptionCategoryAdapter.DetailContent) {
        val category = content.categoryVO
        viewBinding.writeOptionsSelectCategoryTitle.text = when(category.title) {
            "Default" -> viewBinding.root.context.getString(R.string.category_view_holder_title)
            "new" -> viewBinding.root.context.getString(R.string.add_category_body)
            else -> category.title
        }
        viewBinding.root.setOnClickListener {
            viewBinding.writeOptionsSelectCategoryIcon.isVisible = delegate.onItemClick(category.id)
        }
    }
}