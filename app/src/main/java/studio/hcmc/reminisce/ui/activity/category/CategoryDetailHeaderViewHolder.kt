package studio.hcmc.reminisce.ui.activity.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardCommonHeaderBinding

class CategoryDetailHeaderViewHolder (
    private val viewBinding: CardCommonHeaderBinding,
    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
    interface Delegate {
        fun onClick()
    }

    constructor(parent: ViewGroup, delegate: Delegate): this(
        viewBinding = CardCommonHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        delegate = delegate
    )

    fun bind() {
        viewBinding.commonHeaderTitle.text = "Editable Category"


//        viewBinding.commonHeaderTitle.text =
//            intent.getStringExtra("categoryTitle")
        viewBinding.commonHeaderAction1.text = "편집"

        viewBinding.commonHeaderAction1.setOnClickListener {
            delegate.onClick()
        }
    }
}