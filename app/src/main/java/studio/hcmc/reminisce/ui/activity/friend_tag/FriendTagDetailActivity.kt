package studio.hcmc.reminisce.ui.activity.friend_tag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import studio.hcmc.reminisce.databinding.ActivityFriendTagDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.category.ItemDeleteDialog
import studio.hcmc.reminisce.ui.activity.friend_tag.editable.FriendTagEditableDetailActivity
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class FriendTagDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendTagDetailBinding
    private lateinit var adapter: FriendTagAdapter
    private lateinit var friend: FriendVO
    private lateinit var user: UserVO

    private val opponentId by lazy { intent.getIntExtra("opponentId", -1) }

    private val context = this
    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<FriendTagAdapter.Content>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    private val friendTagEditableLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onModifiedResult)
    private val writeDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onWriteDetailResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendTagDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.friendTagDetailAppbar.appbarTitle.text = getString(R.string.header_view_holder_title)
        viewBinding.friendTagDetailAppbar.appbarActionButton1.isVisible = false
        viewBinding.friendTagDetailAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.friendTagDetailItems.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch { loadContents() }
    }

    private suspend fun prepareUser(): UserVO {
        if (!this::user.isInitialized) {
            user = UserExtension.getUser(context)
        }

        return user
    }

    private suspend fun prepareFriend(): FriendVO {
        if (!this::friend.isInitialized) {
            friend = FriendIO.getByUserIdAndOpponentId(user.id, opponentId)
        }

        return friend
    }

    private suspend fun loadContents() = mutex.withLock {
        val user = prepareUser()
        val friend = prepareFriend()
        val lastId = locations.lastOrNull()?.id ?: Int.MAX_VALUE
        val delay = System.currentTimeMillis() - lastLoadedAt - 2000
        if (delay < 0) {
            delay(-delay)
        }

        if (!hasMoreContents) {
            return
        }

        try {
            val fetched = LocationIO.listByUserIdAndOpponentId(user.id, friend.opponentId, lastId)
            for (location in fetched.sortedByDescending { it.id }) {
                this.locations.add(location)
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
        CommonError.onMessageDialog(this, getString(R.string.dialog_error_common_list_body))
    }

    private fun prepareContents(fetched: List<LocationVO>): Int {
        if (contents.lastOrNull() is FriendTagAdapter.ProgressContent) {
            contents.removeLast()
        }

        if (contents.isEmpty()) {
            contents.add(FriendTagAdapter.HeaderContent(friend.nickname!!))
        }

        val group = fetched.groupByTo(HashMap()) { it.createdAt.toString().substring(0, 7) }
        var size = 0
        val lastDetailContent = contents.lastOrNull() as? FriendTagAdapter.DetailContent
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
            contents.add(FriendTagAdapter.DateContent(getString(R.string.card_date_separator, year, month.removePrefix("0"))))
            size += locations.size
            addDetailContents(locations)
        }

        if (hasMoreContents) {
            contents.add(FriendTagAdapter.ProgressContent)
        }

        return size
    }

    private fun addDetailContents(locations: List<LocationVO>) {
        for (location in locations) {
            val content = FriendTagAdapter.DetailContent(
                location = location,
                tags = tagInfo[location.id].orEmpty(),
                friends = friendInfo[location.id].orEmpty()
            )

            contents.add(content)
        }
    }

    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            adapter = FriendTagAdapter(adapterDelegate, headerDelegate, itemDelegate)
            viewBinding.friendTagDetailItems.adapter = adapter
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private val adapterDelegate = object : FriendTagAdapter.Delegate {
        override fun hasMoreContents() = hasMoreContents
        override fun getMoreContents() { CoroutineScope(Dispatchers.IO).launch { loadContents() } }
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : FriendTagHeaderViewHolder.Delegate {
        override fun onEditClick() {
            launchFriendEditableDetail(friend.opponentId, friend.nickname!!)
        }
    }

    private val itemDelegate = object : FriendTagItemViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String) {
            launchWriteDetail(locationId, title)
        }

        override fun onItemLongClick(locationId: Int, position: Int) {
            ItemDeleteDialog(context, deleteDialogDelegate, locationId, position)
        }
    }

    private val deleteDialogDelegate = object : ItemDeleteDialog.Delegate {
        override fun onClick(locationId: Int, position: Int) {
            LocalLogger.v("locationId:$locationId, position:$position")
            deleteContent(locationId, position, findIndexInList(locationId, locations))
        }
    }

    private fun deleteContent(locationId: Int, position: Int, locationIndex: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.delete(locationId) }
            .onSuccess {
                locations.removeAt(locationIndex)
                tagInfo.remove(locationId)
                friendInfo.remove(locationId)
                withContext(Dispatchers.Main) { adapter.notifyItemRemoved(position) }
                viewBinding.friendTagDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun launchFriendEditableDetail(opponentId: Int, nickname: String) {
        val intent = Intent(context, FriendTagEditableDetailActivity::class.java)
            .putExtra("opponentId", opponentId)
            .putExtra("nickname", nickname)
        friendTagEditableLauncher.launch(intent)
    }

    private fun launchModifiedHome() {
        Intent().putExtra("isModified", true).setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private fun onModifiedResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            contents.clear()
            CoroutineScope(Dispatchers.IO).launch { loadContents() }
        }
    }

    private fun launchWriteDetail(locationId: Int, title: String) {
        Log.v("Reminisce", "launchWriteDetail($locationId, $title)")
        val intent = Intent(this, WriteDetailActivity::class.java)
            .putExtra("locationId", locationId)
            .putExtra("title", title)
        writeDetailLauncher.launch(intent)
    }

    private fun onWriteDetailResult(activityResult: ActivityResult) {
        Log.v("Reminisce", "onWriteDetailResult: ${activityResult.data?.getBooleanExtra("isModified", false)}")
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            contents.clear()
            hasMoreContents = true
            lastLoadedAt = 0L
            CoroutineScope(Dispatchers.IO).launch { loadContents() }
        }
    }

    private fun findIndexInList(target: Int, list: ArrayList<LocationVO>): Int {
        var index = -1
        for (vo in list.withIndex()) {
            if (vo.value.id == target) {
                index = vo.index
            }
        }

        return index
    }
}
