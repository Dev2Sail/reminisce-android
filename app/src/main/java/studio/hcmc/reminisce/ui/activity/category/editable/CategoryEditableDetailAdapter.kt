package studio.hcmc.reminisce.ui.activity.category.editable

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class CategoryEditableDetailAdapter(
    private val adapterDelegate: Delegate,

): Adapter<ViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<EditableCategoryDetailContents> {


    }

    sealed interface EditableCategoryDetailContents

    class EditableCategoryDetailSummaryContent(
        val location: LocationVO,
        val friend: FriendVO?,
        val tag: TagVO?
    ): EditableCategoryDetailContents



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = adapterDelegate.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int) = when (adapterDelegate.getItem(position)) {
        is EditableCategoryDetailSummaryContent -> 0
    }

}

//class CategoryEditableDetailAdapter(
//    private val detailContents: List<EditableCategoryDetail>
////    private val adapterDelegate: Delegate
////    private val summaryContents: List<SummaryModal>,
////    private val summaryDelegate: EditableSummaryViewHolder.Delegate
//
//): Adapter<ViewHolder>() {
////    interface Delegate {
////        fun getItemCount(): Int
////        fun getContent(position: Int)
////        fun getMoreContents()
////    }
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = when(viewType) {
//        0 -> EditableSummaryViewHolder(parent)
//
////        1 -> EditableSummaryViewHolder(parent, summaryDelegate)
//        else -> unknownViewType(viewType)
//    }
//
////    override fun getItemCount() = adapterDelegate.getItemCount() + 2
//    override fun getItemCount() = detailContents.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        when(holder) {
//            is EditableSummaryViewHolder -> holder.bind(detailContents[position] as SummaryModal)
//        }
//    }
//
//    override fun getItemViewType(position: Int) = when(detailContents[position]) {
//        is SummaryModal -> 0
//        else -> unknownViewType(position)
//    }
//}
