package studio.hcmc.reminisce.ui.activity.report

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityReportBeachDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class InternalDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityReportBeachDetailBinding
    private lateinit var adapter: InternalDetailAdapter

    private val beachFlag by lazy { intent.getBooleanExtra("beach", false) }
    private val serviceAreaFlag by lazy { intent.getBooleanExtra("serviceArea", false) }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<InternalDetailAdapter.Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityReportBeachDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.reportDetailBeachAppbar.appbarBack.setOnClickListener { finish() }
        if (beachFlag && !serviceAreaFlag) {
            viewBinding.reportDetailBeachAppbar.appbarTitle.text = getString(R.string.report_ocean)
            loadContents(true)
        } else if (!beachFlag && serviceAreaFlag) {
            viewBinding.reportDetailBeachAppbar.appbarTitle.text = getString(R.string.report_service_area)
            loadContents(false)
        }
        viewBinding.reportDetailBeachAppbar.appbarActionButton1.isVisible = false
    }

    private fun loadContents(flag: Boolean) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@InternalDetailActivity)
        val result = runCatching {
            val locationDeferredWrapper = if (flag) {
                async { LocationIO.beachListByUserId(user.id, Int.MAX_VALUE) }
            } else {
                async { LocationIO.serviceAreaListByUserId(user.id, Int.MAX_VALUE) }
            }

            val locationDeferred = locationDeferredWrapper.await()
            for (location in locationDeferred) {
                locations.add(location)
                val tagsDeferred = async { TagIO.listByLocationId(location.id) }
                val friendsDeferred = async { FriendIO.listByUserIdAndLocationId(user.id, location.id) }
                tagInfo[location.id] = tagsDeferred.await()
                friendInfo[location.id] = friendsDeferred.await()
            }
        }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        }
    }

    private fun prepareContents() {
        for ((date, locations) in locations.groupBy { it.createdAt.toString().substring(0, 7) }.entries) {
            val (year, month) = date.split("-")
            contents.add(InternalDetailAdapter.DateContent(getString(R.string.card_date_separator, year, month.trim('0'))))
            for (location in locations.sortedByDescending { it.id }) {
                contents.add(InternalDetailAdapter.DetailContent(location, tagInfo[location.id].orEmpty(), friendInfo[location.id].orEmpty()))
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.reportDetailBeachItems.layoutManager = LinearLayoutManager(this)
        adapter = InternalDetailAdapter(adapterDelegate, summaryDelegate)
        viewBinding.reportDetailBeachItems.adapter = adapter
    }

    private val adapterDelegate = object : InternalDetailAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val summaryDelegate = object : InternalSummaryViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String, position: Int) {
            Intent(this@InternalDetailActivity, WriteDetailActivity::class.java).apply {
                putExtra("locationId", locationId)
                putExtra("title", title)
                putExtra("position", position)
                startActivity(this)
            }
        }

//        override fun onItemLongClick(locationId: Int, position: Int) {
//            SummaryDeleteDialog(this@BeachDetailActivity, deleteDialogDelegate, locationId, position)
//        }
    }

    private fun onSummaryModifiedResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {

        }
    }

//    private suspend fun onPatchSummary(locationId: Int, position: Int) {
//        val user = UserExtension.getUser(this)
//        LocationIO.getById(locationId)
//    }



            /*
            private fun onPatchCategory(categoryId: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@HomeActivity)
        runCatching { CategoryIO.getById(categoryId) }
            .onSuccess {
                val count = CategoryIO.getCountByCategoryIdAndUserId(user.id, categoryId).get("count").asInt
                contents[position] = HomeAdapter.CategoryContent(it, count)
                withContext(Dispatchers.Main) { adapter.notifyItemChanged(position) }
            }.onFailure { LocalLogger.e(it) }
    }
             */

}
//    private val deleteDialogDelegate = object : SummaryDeleteDialog.Delegate {
//        override fun onClick(locationId: Int, position: Int) {
//            LocalLogger.v("locationId:$locationId, position:$position")
//            var locationIdx = -1
//            for (item in locations.withIndex()) {
//                if (item.value.id == locationId) {
//                    locationIdx = item.index
//                }
//            }
//            deleteContent(locationId, position, locationIdx)
//        }
//    }

//    // 삭제하면 HomeActivity에 어케 전달할 건데
//    private fun deleteContent(locationId: Int, position: Int, locationIdx: Int) = CoroutineScope(Dispatchers.IO).launch {
//        runCatching { LocationIO.delete(locationId) }
//            .onSuccess {
//                locations.removeAt(locationIdx)
//                tagInfo.remove(locationId)
//                friendInfo.remove(locationId)
//                withContext(Dispatchers.Main) { adapter.notifyItemRemoved(position) }
//            }.onFailure { LocalLogger.e(it) }
//    }