package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
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

   constructor(parent: ViewGroup, delegate: Delegate): this(
       viewBinding = CardCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
       delegate = delegate
   )

     fun bind() {
         for (category in delegate.categories) {
             viewBinding.homeCategoryTitle.text = category.title
//             when(category.title) {
//                 is Defualt -> viewBinding.homeCategoryTitle.text = "한눈에 보기"
//             }

             viewBinding.homeCategoryAction1.setOnClickListener {
                 delegate.onCategoryClick(category)
             }
         }
         viewBinding.root.setOnLongClickListener{
             DeleteCategoryDialog(it.context)
//                 return@setOnLongClickListener true
             false
         }
     }
}
