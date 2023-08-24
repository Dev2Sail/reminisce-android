package studio.hcmc.reminisce.ui.activity.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardCommonHeaderBinding

class HeaderViewHolder(
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
        viewBinding.commonHeaderTitle.text = viewBinding.root.context.getText(R.string.header_view_holder_title)
        viewBinding.commonHeaderAction1.text = viewBinding.root.context.getText(R.string.header_view_holder_action)

        viewBinding.commonHeaderAction1.setOnClickListener {
            delegate.onClick()
        }
    }
}