package studio.hcmc.reminisce.ui.view

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex

class ScrollToLoadManager(
    private val lifecycleOwner: LifecycleOwner,
    private val size: () -> Int,
    private val action: () -> Unit,
    private val delay: Long = 2000,
){
    enum class LoadResult {
        /** 더 이상 불러올 항목이 없음 */
        NO_MORE_CONTENT,
        /** 이미 불러오고 있음 */
        ALREADY_LOADING,
        /** 이미 예약되어 있음 */
        ALREADY_RESERVED,
        /** 예약됨 */
        RESERVED,
        /** 시작됨 */
        STARTED,
    }

    private var reservedTask: Job? = null
    var hasMoreContents: Boolean = true
    var listSize: Int = size(); private set
    var lastTimestamp: Long = 0L
    private var isLoading: Boolean = false
    private var mutex = Mutex()

//    fun loadWithoutResult() {
//        load()
//    }

//    fun load(): LoadResult {
//        if (!hasMoreContents) {
//            return LoadResult.NO_MORE_CONTENT
//        } else if (isLoading) {
//            return LoadResult.ALREADY_LOADING
//        }
//
//        val elapsed = System.currentTimeMillis() - lastTimestamp
//        if (elapsed < 2000) {
//            if (reservedTask != null) {
//                return LoadResult.ALREADY_RESERVED
//            }
//
////            reservedTask = lifecycleOwner.
//            return LoadResult.RESERVED
//        } else {
//            return
//
//        }
//    }

    private fun startLoading() {
//        lifecycleOwner.

    }
}