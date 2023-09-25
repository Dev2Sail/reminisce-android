package studio.hcmc.reminisce.ui.activity.writer.options

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.LayoutWriteOptionsSelectCategoryItemBinding
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.vo.category.CategoryVO

class WriteOptionSelectCategoryItemViewHolder(
    private val viewBinding: LayoutWriteOptionsSelectCategoryItemBinding,
    private val delegate: Delegate
): ViewHolder(viewBinding.root) {
    interface Delegate: SingleTypeAdapterDelegate<CategoryVO> {
        val currentCategoryId: Int
        fun onItemClick(categoryId: Int): Boolean
    }

    constructor(parent: ViewGroup, delegate: Delegate) : this(
        viewBinding = LayoutWriteOptionsSelectCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )


    fun bind(category: CategoryVO) {
        if (category.title == "Default") {
            viewBinding.writeOptionsSelectCategoryTitle.text = viewBinding.root.context.getText(R.string.category_view_holder_title)
        } else {
            viewBinding.writeOptionsSelectCategoryTitle.text = category.title
        }
        // 단일 선택 구현



        viewBinding.apply {

            root.setOnClickListener {
                if (!delegate.onItemClick(category.id)) {
                    writeOptionsSelectCategoryIcon.isVisible = true
                }

                Log.v("result","=== ${delegate.onItemClick(category.id)} ")
    //                else if (!delegate.onItemClick(category.id)) {
//                    writeOptionsSelectCategoryIcon.isVisible = true
//                }
            }

        }
    }
}