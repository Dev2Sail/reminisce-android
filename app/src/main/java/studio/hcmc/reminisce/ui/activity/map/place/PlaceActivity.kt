package studio.hcmc.reminisce.ui.activity.map.place

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
import studio.hcmc.reminisce.databinding.ActivityPlaceBinding
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

class PlaceActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityPlaceBinding
    private lateinit var adapter: PlaceAdapter
    private lateinit var user: UserVO

    private val placeName by lazy { intent.getStringExtra("placeName") }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<PlaceAdapter.Content>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.placeAppbar.appbarTitle.text = getString(R.string.nav_main_map)
        viewBinding.placeAppbar.appbarActionButton1.isVisible = false
        viewBinding.placeAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.placeItems.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch { placeName?.let { loadContents(it) } }
    }

    private suspend fun prepareUser(): UserVO {
        if (!this::user.isInitialized) {
            user = UserExtension.getUser(this)
        }

        return user
    }

    private suspend fun loadContents(placeName: String) = mutex.withLock {
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
            val fetched = LocationIO.listByUserIdAndTitle(user.id, placeName, lastId)
            for (location in fetched.sortedByDescending { it.id }) {
                locations.add(location)
                tagInfo[location.id] = TagIO.listByLocationId(location.id)
                friendInfo[location.id] = FriendIO.listByUserIdAndLocationId(user.id, location.id)
            }

            hasMoreContents = fetched.size >= 10
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
        if (contents.lastOrNull() is PlaceAdapter.ProgressContent) {
            contents.removeLast()
        }

        if (contents.isEmpty()) {
            placeName?.let { contents.add(PlaceAdapter.HeaderContent(it)) }
        }

        val group = fetched.groupByTo(HashMap()) { it.createdAt.toString().substring(0, 7) }
        var size = 0
        val lastDetailContent = contents.lastOrNull() as? PlaceAdapter.DetailContent
        if (lastDetailContent != null) {
            val list = group.remove(lastDetailContent.location.createdAt.toString().substring(0, 7))
            if (list != null) {
                size += list.size
                addDetailContent(list)
            }
        }

        for ((date, locations) in group) {
            val (year, month) = date.split("-")
            size++
            contents.add(PlaceAdapter.DateContent(getString(R.string.card_date_separator, year, month.removePrefix("0"))))
            size += locations.size
            addDetailContent(locations)
        }

        if (hasMoreContents) {
            contents.add(PlaceAdapter.ProgressContent)
        }

        return size
    }

    private fun addDetailContent(locations: List<LocationVO>) {
        for (location in locations) {
            val content = PlaceAdapter.DetailContent(
                location = location,
                tags = tagInfo[location.id].orEmpty(),
                friends = friendInfo[location.id].orEmpty()
            )

            contents.add(content)
        }
    }

    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            adapter = PlaceAdapter(adapterDelegate, summaryDelegate)
            viewBinding.placeItems.adapter = adapter
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private val adapterDelegate = object : PlaceAdapter.Delegate {
        override fun hasMoreContents() = hasMoreContents
        override fun getMoreContents() {
            CoroutineScope(Dispatchers.IO).launch {
                placeName?.let { loadContents(it) }
            }
        }
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val summaryDelegate = object : PlaceItemViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String) {
            moveToWriteDetail(locationId, title)
        }
    }

    private fun moveToWriteDetail(locationId: Int, title: String) {
        Intent(this@PlaceActivity, WriteDetailActivity::class.java).apply {
            putExtra("locationId", locationId)
            putExtra("title", title)
            startActivity(this)
        }
    }
}