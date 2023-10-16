package studio.hcmc.reminisce.ui.activity.category.editable

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.activity.category.EditableCategoryDetail
import studio.hcmc.reminisce.ui.activity.category.SummaryModal
import studio.hcmc.reminisce.ui.view.unknownViewType

class CategoryEditableDetailAdapter(
    private val detailContents: List<EditableCategoryDetail>
//    private val adapterDelegate: Delegate
//    private val summaryContents: List<SummaryModal>,
//    private val summaryDelegate: EditableSummaryViewHolder.Delegate

): Adapter<ViewHolder>() {
//    interface Delegate {
//        fun getItemCount(): Int
//        fun getContent(position: Int)
//        fun getMoreContents()
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = when(viewType) {
        0 -> EditableSummaryViewHolder(parent)

//        1 -> EditableSummaryViewHolder(parent, summaryDelegate)
        else -> unknownViewType(viewType)
    }

//    override fun getItemCount() = adapterDelegate.getItemCount() + 2
    override fun getItemCount() = detailContents.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder) {
            is EditableSummaryViewHolder -> holder.bind(detailContents[position] as SummaryModal)
        }
    }

    override fun getItemViewType(position: Int) = when(detailContents[position]) {
        is SummaryModal -> 0
        else -> unknownViewType(position)
    }
}
