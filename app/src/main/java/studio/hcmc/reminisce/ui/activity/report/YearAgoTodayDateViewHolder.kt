package studio.hcmc.reminisce.ui.activity.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import studio.hcmc.reminisce.databinding.CardDateSeparatorBinding

class YearAgoTodayDateViewHolder(private val viewBinding: CardDateSeparatorBinding) : RecyclerView.ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup): this(
        CardDateSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(content: YearAgoTodayAdapter.DateContent) {
        if (content.body.isNullOrEmpty()) {
            viewBinding.cardDateSeparator.isGone = true
        } else {
            viewBinding.cardDateSeparator.isVisible = true
            viewBinding.cardDateSeparator.text = content.body
        }
    }
}