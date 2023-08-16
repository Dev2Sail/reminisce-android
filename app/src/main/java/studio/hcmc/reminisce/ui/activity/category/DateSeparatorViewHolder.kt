package studio.hcmc.reminisce.ui.activity.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import studio.hcmc.reminisce.databinding.CardDateSeparatorBinding
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class DateSeparatorViewHolder (
    private val viewBinding: CardDateSeparatorBinding
) : ViewHolder(viewBinding.root) {

    constructor(parent: ViewGroup): this(
        viewBinding = CardDateSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    fun bind() {
        val dateFormat = SimpleDateFormat("yyyy-MM", Locale("ko", "KR"))
        var now = dateFormat.format(Date(System.currentTimeMillis()))
        viewBinding.cardDateSeparator.text = now
        viewBinding.root.isClickable = false
    }
}