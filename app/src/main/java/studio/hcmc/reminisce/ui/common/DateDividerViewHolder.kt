package studio.hcmc.reminisce.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardDateSeparatorBinding

class DateDividerViewHolder(
    private val viewBinding: CardDateSeparatorBinding
):ViewHolder(viewBinding.root) {

    constructor(parent: ViewGroup): this(
        viewBinding = CardDateSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    // year, month 구분
    fun bind(body: String) {
        viewBinding.cardDateSeparator.text = body

    }
}
/*
SELECT *
FROM `location`
WHERE
	YEAR(created_at) = 2023 AND
    MONTH(created_at) = 6 AND
    is_deleted = 0
ORDER BY created_at DESC
LIMIT 10
 */