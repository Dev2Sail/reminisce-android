package studio.hcmc.reminisce.ui.activity.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardCategoryDetailSummaryItemBinding

class SummaryItemViewHolder(
    private val viewBinding: CardCategoryDetailSummaryItemBinding
//    private val delegate: Delegate
) : ViewHolder(viewBinding.root) {
//    interface Delegate {
//        // 장소
//        fun getTitle()
//
//        // card_title, 장소의 주소
//        fun getAddress()
//
//        // 방문 날짜
//        fun getVisitedAt()
//
//        // 등록된 태그
//        fun getTag()
//
//        // 함께 다녀온 사람 태그
//        fun getFriendTag()
//    }

    constructor(parent: ViewGroup): this(
        viewBinding = CardCategoryDetailSummaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind() {

    }

}
