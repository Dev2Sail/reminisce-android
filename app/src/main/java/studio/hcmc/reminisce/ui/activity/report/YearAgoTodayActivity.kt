package studio.hcmc.reminisce.ui.activity.report

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityYearAgoTodayBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class YearAgoTodayActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityYearAgoTodayBinding
    private lateinit var adapter: YearAgoTodayAdapter
    private val date by lazy { intent.getStringExtra("date") }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<YearAgoTodayAdapter.Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityYearAgoTodayBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.reportTodayAppbar.appbarTitle.text = date
        viewBinding.reportTodayAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.reportTodayAppbar.appbarActionButton1.isVisible = false
        loadContents(date!!)
    }

    private fun loadContents(date: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@YearAgoTodayActivity)
        val result = runCatching {
            val locationDeferred = async { LocationIO.yearAgoTodayByUserIdAndDate(user.id, date, Int.MAX_VALUE) }
            val locationResult = locationDeferred.await()
            for (location in locationResult) {
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
            contents.add(YearAgoTodayAdapter.DateContent(getString(R.string.card_date_separator, year, month.trim('0'))))
            for (location in locations.sortedByDescending { it.id }) {
                contents.add(YearAgoTodayAdapter.DetailContent(
                    location,
                    tagInfo[location.id].orEmpty(),
                    friendInfo[location.id].orEmpty()
                ))
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.reportTodayItems.layoutManager = LinearLayoutManager(this)
        adapter = YearAgoTodayAdapter(adapterDelegate, itemDelegate)
        viewBinding.reportTodayItems.adapter = adapter
    }

    private val adapterDelegate = object : YearAgoTodayAdapter.Delegate {
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