package studio.hcmc.reminisce.ui.activity.tag

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardCategoryDetailSummaryItemBinding

class TagDetailItemViewHolder(
    private val viewBinding: CardCategoryDetailSummaryItemBinding

): ViewHolder(viewBinding.root) {

    fun bind(content: TagDetailAdapter.TagDetailItemContent) {
        // markerEmoji or friend nickname
        val tagDetailItem = (content as? TagDetailAdapter.TagDetailItemContent)

        viewBinding.cardCategoryDetailSummaryItemTitle.text = ""
    }
}