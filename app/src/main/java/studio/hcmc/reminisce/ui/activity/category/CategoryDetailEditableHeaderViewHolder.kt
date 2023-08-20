package studio.hcmc.reminisce.ui.activity.category

import android.widget.Toast
import studio.hcmc.reminisce.databinding.CardCategoryDetailHeaderBinding

class CategoryDetailEditableHeaderViewHolder (
    private val viewBinding: CardCategoryDetailHeaderBinding
) {
    fun bind() {
        viewBinding.categoryDetailHeaderAction1.text = "완료"
//        viewBinding.categoryDetailHeaderAction1.text = viewBinding.root.context.getText(R.string.dialog_save)

        viewBinding.categoryDetailHeaderAction1.setOnClickListener {
            viewBinding.categoryDetailHeaderAction1.text = "편집"
            Toast.makeText(viewBinding.root.context, "clicked action1", Toast.LENGTH_SHORT).show()
        }
    }
}