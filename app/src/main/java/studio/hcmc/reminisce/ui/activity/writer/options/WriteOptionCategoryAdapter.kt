package studio.hcmc.reminisce.ui.activity.writer.options

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.vo.category.CategoryVO

class WriteOptionCategoryAdapter(
    private val adapterDelegate: Delegate,
    private val itemDelegate: WriteOptionCategoryItemViewHolder.Delegate,
): Adapter<WriteOptionCategoryItemViewHolder>() {
    interface Delegate: SingleTypeAdapterDelegate<Content>
    sealed interface Content
    data class DetailContent(val categoryVO: CategoryVO): Content
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WriteOptionCategoryItemViewHolder(parent, itemDelegate)
    override fun getItemCount() = adapterDelegate.getItemCount()
    override fun onBindViewHolder(holder: WriteOptionCategoryItemViewHolder, position: Int) = holder.bind(adapterDelegate.getItem(position) as DetailContent)
}