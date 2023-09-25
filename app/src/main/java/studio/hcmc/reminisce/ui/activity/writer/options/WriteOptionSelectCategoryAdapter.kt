package studio.hcmc.reminisce.ui.activity.writer.options

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter

class WriteOptionSelectCategoryAdapter(
    private val itemDelegate: WriteOptionSelectCategoryItemViewHolder.Delegate,
    private var selectedItemId: Int = -1

): Adapter<WriteOptionSelectCategoryItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WriteOptionSelectCategoryItemViewHolder(parent, itemDelegate)
    override fun getItemCount() = itemDelegate.getItemCount()

    override fun onBindViewHolder(holder: WriteOptionSelectCategoryItemViewHolder, position: Int) = holder.bind(itemDelegate.getItem(position))

}