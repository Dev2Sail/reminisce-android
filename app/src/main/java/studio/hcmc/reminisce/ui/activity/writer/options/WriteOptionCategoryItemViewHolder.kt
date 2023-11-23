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
        fun onItemClick(categoryId: Int, position: Int): Boolean

        fun validate(categoryId: Int): Boolean
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
        // 기본 선택된 카테고리
        viewBinding.writeOptionsSelectCategoryIcon.isVisible = delegate.validate(category.id)
        viewBinding.root.setOnClickListener {
            viewBinding.writeOptionsSelectCategoryIcon.isVisible = delegate.onItemClick(category.id, bindingAdapterPosition)
            viewBinding.writeOptionsSelectCategoryIcon.isVisible = delegate.validate(category.id)
        }


        // 변경할 카테고리 선택 시 !validate && onItemClick 이면 visible?

//        viewBinding.root.setOnClickListener {
////            viewBinding.writeOptionsSelectCategoryIcon.isVisible = delegate.onItemClick(category.id, bindingAdapterPosition)
//            delegate.onItemClick(category.id, bindingAdapterPosition)
//            viewBinding.writeOptionsSelectCategoryIcon.isVisible = delegate.validate(category.id)
//        }
    }
}
/*
1. intent로 받아온 categoryId와 동일한 경우 visible true
2. 다른 item 선택 시 해당 item만 visible true


map? categoryId, position
new data class?
 */