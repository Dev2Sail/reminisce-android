package studio.hcmc.reminisce.ui.view

interface SingleTypeAdapterDelegate<T> {
    fun getItemCount(): Int
    fun getItem(position: Int) : T
    fun getItemOrNull(position: Int): T? = throw UnsupportedOperationException()
}