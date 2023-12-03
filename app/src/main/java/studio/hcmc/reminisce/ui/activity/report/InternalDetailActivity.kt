package studio.hcmc.reminisce.ui.activity.report

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityReportBeachDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class InternalDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityReportBeachDetailBinding
    private lateinit var adapter: InternalDetailAdapter
    private lateinit var user: UserVO

    private val beachFlag by lazy { intent.getBooleanExtra("beach", false) }
    private val serviceAreaFlag by lazy { intent.getBooleanExtra("serviceArea", false) }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<InternalDetailAdapter.Content>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityReportBeachDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.reportDetailBeachAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.reportDetailBeachItems.layoutManager = LinearLayoutManager(this)
        viewBinding.reportDetailBeachAppbar.appbarTitle.text = getString(R.string.nav_main_report)

        if (beachFlag && !serviceAreaFlag) {
            viewBinding.reportDetailBeachAppbar.appbarTitle.text = getString(R.string.report_ocean)
        } else if (!beachFlag && serviceAreaFlag) {
            viewBinding.reportDetailBeachAppbar.appbarTitle.text = getString(R.string.report_service_area)
        }
        viewBinding.reportDetailBeachAppbar.appbarActionButton1.isVisible = false
        CoroutineScope(Dispatchers.IO).launch { loadContents() }
    }

    private suspend fun prepareUser(): UserVO {
        if (!this::user.isInitialized) {
            user = UserExtension.getUser(this)
        }

        return user
    }

    private suspend fun fetch(): List<LocationVO> {
        val user = prepareUser()
        val lastId = locations.lastOrNull()?.id ?: Int.MAX_VALUE
        return when {
            beachFlag && !serviceAreaFlag -> LocationIO.beachListByUserId(user.id, lastId)
            !beachFlag && serviceAreaFlag -> LocationIO.serviceAreaListByUserId(user.id, lastId)
            else -> { throw IOException() }
        }
    }

    private suspend fun loadContents() = mutex.withLock {
        val delay = System.currentTimeMillis() - lastLoadedAt - 2000
        if (delay < 0) {
            delay(-delay)
        }

        if (!hasMoreContents) {
            return
        }

        try {
            val fetched = fetch().sortedByDescending { it.id }
            for (location in fetched) {
                locations.add(location)
                tagInfo[location.id] = TagIO.listByLocationId(location.id)
                friendInfo[location.id] = FriendIO.listByUserIdAndLocationId(user.id, location.id)
            }

            hasMoreContents = fetched.size > 10
            val preSize = contents.size
            val size = prepareContents(fetched)
            withContext(Dispatchers.Main) { onContentsReady(preSize, size) }
            lastLoadedAt = System.currentTimeMillis()
        } catch (e: Throwable) {
            LocalLogger.e(e)
            withContext(Dispatchers.Main) { onError() }
        }
    }

    private fun onError() {
        CommonError.onMessageDialog(this,  getString(R.string.dialog_error_common_list_body))
    }

    private fun prepareContents(fetched: List<LocationVO>): Int {
        if (contents.lastOrNull() is InternalDetailAdapter.ProgressContent) {
            contents.removeLast()
        }

        if (contents.isEmpty()) {
            when {
                beachFlag && !serviceAreaFlag -> contents.add(InternalDetailAdapter.HeaderContent(getString(R.string.report_ocean)))
                !beachFlag && serviceAreaFlag -> contents.add(InternalDetailAdapter.HeaderContent(getString(R.string.report_service_area)))
            }
        }

        val group = fetched.groupByTo(HashMap()) { it.createdAt.toString().substring(0, 7) }
        var size = 0
        val lastDetailContent = contents.lastOrNull() as? InternalDetailAdapter.DetailContent
        if (lastDetailContent != null) {
            val list = group.remove(lastDetailContent.location.createdAt.toString().substring(0, 7))
            if (list != null) {
                size += list.size
                addDetailContents(list)
            }
        }

        for ((date, locations) in group) {
            val (year, month) = date.split("-")
            size++
            contents.add(InternalDetailAdapter.DateContent(getString(R.string.card_date_separator, year, month.removePrefix("0"))))
            size += locations.size
            addDetailContents(locations)
        }

        if (hasMoreContents) {
            contents.add(InternalDetailAdapter.ProgressContent)
        }

        return size
    }

    private fun addDetailContents(locations: List<LocationVO>) {
        for (location in locations) {
            val content = InternalDetailAdapter.DetailContent(
                location = location,
                tags = tagInfo[location.id].orEmpty(),
                friends = friendInfo[location.id].orEmpty()
            )

            contents.add(content)
        }
    }

    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            adapter = InternalDetailAdapter(adapterDelegate, summaryDelegate)
            viewBinding.reportDetailBeachItems.adapter = adapter
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private val adapterDelegate = object : InternalDetailAdapter.Delegate {
        override fun hasMoreContents() = hasMoreContents
        override fun getMoreContents() { CoroutineScope(Dispatchers.IO).launch { loadContents() } }
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val summaryDelegate = object : InternalItemViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String, position: Int) {
            moveToWriteDetail(locationId, title, position)
        }

//        override fun onItemLongClick(locationId: Int, position: Int) {
//            SummaryDeleteDialog(this@BeachDetailActivity, deleteDialogDelegate, locationId, position)
//        }
    }

    private fun moveToWriteDetail(locationId: Int, title: String, position: Int) {
        Intent(this@InternalDetailActivity, WriteDetailActivity::class.java).apply {
            putExtra("locationId", locationId)
            putExtra("title", title)
            putExtra("position", position)
            startActivity(this)
        }
    }
}
