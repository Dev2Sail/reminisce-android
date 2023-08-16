package studio.hcmc.reminisce.ui.view

import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

fun <VH> Adapter<VH>.unknownViewType(viewType: Int): Nothing where VH: ViewHolder {
    throw IllegalArgumentException("Unknown viewType: $viewType")
}

fun <VH> Adapter<VH>.unknownViewHolder(holder: ViewHolder, position: Int): Nothing where VH: ViewHolder {
    throw IllegalArgumentException("Unknown viewHolder($holder) - position($position)")
}