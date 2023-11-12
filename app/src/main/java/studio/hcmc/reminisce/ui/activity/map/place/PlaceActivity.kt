package studio.hcmc.reminisce.ui.activity.map.place

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityPlaceBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class PlaceActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityPlaceBinding
    private lateinit var adapter: PlaceAdapter
    private lateinit var locations: List<LocationVO>

    private val placeEditableLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onModifiedResult)
    private val location by lazy { intent.getStringExtra("location") }

    private val users = HashMap<Int, UserVO>()
    private val friends = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tags = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<PlaceAdapter.Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.placeAppbar.appbarActionButton1.isVisible = false
        viewBinding.placeAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.placeAppbar.appbarTitle.text = getString(R.string.nav_main_map)
        location?.let { loadLocations(it) }
    }

    private fun loadLocations(value: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@PlaceActivity)
        val result = runCatching { LocationIO.listByUserIdAndTitle(user.id, value) }
            .onSuccess { it ->
                locations = it
                it.forEach {
                    friends[it.id] = FriendIO.listByUserIdAndLocationId(user.id, it.id)
                    tags[it.id] = TagIO.listByLocationId(it.id)
                }
                friends.values.forEach {
                    for (friend in it) {
                        if (friend.nickname == null) {
                            val opponent = UserIO.getById(friend.opponentId)
                            users[opponent.id] = opponent
                        }
                    }
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        }
    }

    private fun prepareContents() {
        location?.let { contents.add(PlaceAdapter.HeaderContent(it)) }
        for ((date, locations) in locations.groupBy { it.createdAt.toString().substring(0, 7) }.entries) {
            val (year, month) = date.split("-")
            contents.add(PlaceAdapter.DateContent(getString(R.string.card_date_separator, year, month.trim('0'))))
            for (location in locations.sortedByDescending { it.id }) {
                contents.add(PlaceAdapter.DetailContent(location, tags[location.id].orEmpty(), friends[location.id].orEmpty()))
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.placeItems.layoutManager = LinearLayoutManager(this)
        adapter = PlaceAdapter(adapterDelegate, headerDelegate, summaryDelegate)
        viewBinding.placeItems.adapter = adapter
    }

    private val adapterDelegate = object : PlaceAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : PlaceHeaderViewHolder.Delegate {
        override fun onEditClick() {
//            val intent = Intent().putExtra("title", location)
//            placeEditableLauncher.launch(intent)
        }
    }

    private val summaryDelegate = object : PlaceSummaryViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String) {
            Intent(this@PlaceActivity, WriteDetailActivity::class.java).apply {
                putExtra("locationId", locationId)
                putExtra("title", title)
                startActivity(this)
            }
        }

        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }
    }

    private fun onModifiedResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            contents.removeAll { it is PlaceAdapter.Content }
            location?.let { loadLocations(it) }
        }
    }
}