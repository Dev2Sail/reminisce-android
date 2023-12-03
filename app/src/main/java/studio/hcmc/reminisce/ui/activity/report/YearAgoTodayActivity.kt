package studio.hcmc.reminisce.ui.activity.report

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityYearAgoTodayBinding
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

class YearAgoTodayActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityYearAgoTodayBinding
    private lateinit var adapter: YearAgoTodayAdapter
    private lateinit var user: UserVO
    private val date by lazy { intent.getStringExtra("date") }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<YearAgoTodayAdapter.Content>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityYearAgoTodayBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.reportTodayAppbar.appbarTitle.text = getString(R.string.nav_main_report)
        viewBinding.reportTodayAppbar.appbarActionButton1.isVisible = false
        viewBinding.reportTodayAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.reportTodayItems.layoutManager = LinearLayoutManager(this)
        if (date != null) {
            CoroutineScope(Dispatchers.IO).launch { loadContents() }
        }
    }

    private suspend fun prepareUser(): UserVO {
        if (!this::adapter.isInitialized) {
            user = UserExtension.getUser(this)
        }

        return user
    }

    private suspend fun loadContents() = mutex.withLock {
        val user = prepareUser()
        val lastId = locations.lastOrNull()?.id ?: Int.MAX_VALUE
        val delay = System.currentTimeMillis() - lastLoadedAt - 2000
        if (delay < 0) {
            delay(-delay)
        }

        if (!hasMoreContents) {
            return
        }

        try {
            val fetched = LocationIO.yearAgoTodayByUserIdAndDate(user.id, date!!, lastId)
            for (location in fetched.sortedByDescending { it.id }) {
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
        if (contents.lastOrNull() is YearAgoTodayAdapter.ProgressContent) {
            contents.removeLast()
        }

        if (contents.isEmpty()) {
            contents.add(YearAgoTodayAdapter.HeaderContent(date!!))
        }

        var size = 0
        addDetailContents(fetched.sortedByDescending { it.id })
        size += fetched.size

        if (hasMoreContents) {
            contents.add(YearAgoTodayAdapter.ProgressContent)
        }

        return  size
    }

    private fun addDetailContents(locations: List<LocationVO>) {
        for (location in locations) {
            val content = YearAgoTodayAdapter.DetailContent(
                location = location,
                tags = tagInfo[location.id].orEmpty(),
                friends = friendInfo[location.id].orEmpty()
            )

            contents.add(content)
        }
    }


    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            adapter = YearAgoTodayAdapter(adapterDelegate, itemDelegate)
            viewBinding.reportTodayItems.adapter = adapter
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private val adapterDelegate = object : YearAgoTodayAdapter.Delegate {
        override fun hasMoreContents() = hasMoreContents
        override fun getMoreContents() {
            if (date != null) {
                CoroutineScope(Dispatchers.IO).launch { loadContents() }
            }
        }
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val itemDelegate = object :YearAgoItemViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String, position: Int) {
            moveToWriteDetail(locationId, title, position)
        }
    }

    private fun moveToWriteDetail(locationId: Int, title: String, position: Int) {
        Intent(this, WriteDetailActivity::class.java).apply {
            putExtra("locationId", locationId)
            putExtra("title", title)
            putExtra("position", position)
            startActivity(this)
        }
    }
}