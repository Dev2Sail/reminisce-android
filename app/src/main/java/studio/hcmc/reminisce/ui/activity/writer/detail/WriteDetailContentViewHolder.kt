package studio.hcmc.reminisce.ui.activity.writer.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.CardWriteDetailContentBinding

class WriteDetailContentViewHolder(
    private val viewBinding: CardWriteDetailContentBinding
): ViewHolder(viewBinding.root) {
    constructor(parent: ViewGroup): this(
        viewBinding = CardWriteDetailContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind(content: WriteDetailAdapter.DetailContent) {
        val location = content.location
        val addressBuilder = StringBuilder()
        // TODO 좌표 -> 주소로 변환 후 getString에서 정해둔 포맷으로 넣으셈! (date_separator) 참고
        addressBuilder.append(location.latitude)
        addressBuilder.append(location.longitude)

        val (year, month, day) = location.visitedAt.split("-")
        viewBinding.apply {
            cardWriteDetailContentVisitedAt.writeDetailItemBody.text = viewBinding.root.context.getString(R.string.card_visited_at, year, month.trim('0'), day.trim('0'))
            cardWriteDetailContentLocation.writeDetailItemBody.text = addressBuilder.toString()
            cardWriteDetailContentLocation.writeDetailItemIcon.setImageResource(R.drawable.round_location_on_16)
        }

        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.cardWriteDetailContentMarkerEmoji.apply {
                root.isVisible = true
                writeDetailItemIcon.setImageResource(R.drawable.round_add_reaction_16)
                writeDetailItemBody.text = location.markerEmoji
            }
        } else {
            viewBinding.cardWriteDetailContentMarkerEmoji.root.isGone = true
        }

        if (location.body.isNotEmpty()) {
            viewBinding.cardWriteDetailContent.apply {
                isVisible = true
                text = location.body
            }
            viewBinding.cardWriteDetailContentDivider2.isVisible = true
        } else {
            viewBinding.cardWriteDetailContent.isGone = true
            viewBinding.cardWriteDetailContentDivider2.isGone = true
        }
    }
}