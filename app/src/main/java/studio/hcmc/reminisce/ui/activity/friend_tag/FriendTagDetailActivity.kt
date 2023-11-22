package studio.hcmc.reminisce.ui.activity.friend_tag

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityFriendTagDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.category.SummaryDeleteDialog
import studio.hcmc.reminisce.ui.activity.friend_tag.editable.FriendTagEditableDetailActivity
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class FriendTagDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendTagDetailBinding
    private lateinit var adapter: FriendTagAdapter
    private lateinit var friend: FriendVO

    private val friendTagEditableLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onModifiedResult)
    private val opponentId by lazy { intent.getIntExtra("opponentId", -1) }
    private val nickname by lazy { intent.getStringExtra("nickname") }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<FriendTagAdapter.Content>()

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

        try {
            if (CoroutineScope(Dispatchers.IO).launch { testLoad1() }.isCompleted) {
                prepareContents()
                CoroutineScope(Dispatchers.Main).launch{ onContentsReady() }
                // ...?
            }

        } catch (e: Throwable) {
            LocalLogger.e(e)
        }
    }

    private suspend fun testLoad1() = coroutineScope {
        val user = UserExtension.getUser(this@FriendTagDetailActivity)
        // friend -> locations -> tags & friends => async
        val friendDeferred = async { FriendIO.getByUserIdAndOpponentId(user.id, opponentId) }
        friend = friendDeferred.await()

        val locationsDeferred = async { LocationIO.listByUserIdAndOpponentId(user.id, friend.opponentId) }
//        locations = locationsDeferred.await()
        locations.addAll(locationsDeferred.await())

        for (location in locations) {
            val tagsDeferred = async { TagIO.listByLocationId(location.id) }
            val friendsDeferred = async { FriendIO.listByUserIdAndLocationId(user.id, location.id) }
            tagInfo[location.id] = tagsDeferred.await()
            friendInfo[location.id] = friendsDeferred.await()
        }
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendTagDetailActivity)
        val result = runCatching { LocationIO.listByUserIdAndOpponentId(user.id, friend.opponentId) }
            .onSuccess { it ->
//                locations = it
                locations.addAll(it)
                it.forEach {
                    tagInfo[it.id] = TagIO.listByLocationId(it.id)
                    friendInfo[it.id] = FriendIO.listByUserIdAndLocationId(user.id, it.id)
                }
            }.onFailure { LocalLogger.e(it) }

        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        } else {
            CommonError.onMessageDialog(this@FriendTagDetailActivity, getString(R.string.dialog_error_common_list_body))
        }
    }

    private fun prepareContents() {
        contents.add(FriendTagAdapter.HeaderContent(nickname!!))
        for ((date, locations) in locations.groupBy { it.createdAt.toString().substring(0, 7) }.entries) {
            val (year, month) = date.split("-")
            contents.add(FriendTagAdapter.DateContent(getString(R.string.card_date_separator, year, month.trim('0'))))
            for (location in locations.sortedByDescending { it.id }) {
                contents.add(FriendTagAdapter.DetailContent(
                    location,
                    tagInfo[location.id].orEmpty(),
                    friendInfo[location.id].orEmpty()
                ))
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.friendTagDetailItems.layoutManager = LinearLayoutManager(this)
        adapter = FriendTagAdapter(adapterDelegate, headerDelegate, summaryDelegate)
        viewBinding.friendTagDetailItems.adapter = adapter
    }

    private val adapterDelegate = object : FriendTagAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : FriendTagHeaderViewHolder.Delegate {
        override fun onEditClick() {
            val intent = Intent(this@FriendTagDetailActivity, FriendTagEditableDetailActivity::class.java)
                .putExtra("opponentId", friend.opponentId)
                .putExtra("nickname", friend.nickname)
            friendTagEditableLauncher.launch(intent)
        }
    }

    private val summaryDelegate = object : FriendTagSummaryViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String) {
            // TODO intent result
            // 편집 시 원래 저장돼있던 내용 고대로 들어가야지
            Intent(this@FriendTagDetailActivity, WriteDetailActivity::class.java).apply {
                putExtra("locationId", locationId)
                putExtra("title", title)
                startActivity(this)
            }
        }

        override fun onItemLongClick(locationId: Int, position: Int) {
            SummaryDeleteDialog(this@FriendTagDetailActivity, deleteDialogDelegate, locationId, position)
        }
    }

    private val deleteDialogDelegate = object : SummaryDeleteDialog.Delegate {
        override fun onItemClick(locationId: Int, position: Int) {
            deleteContent(locationId, position)
        }
    }

    private fun deleteContent(locationId: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendTagDetailActivity)
        runCatching { LocationIO.delete(locationId) }
            .onSuccess {
                locations.removeAt(position)
                tagInfo.remove(locationId)
                friendInfo.remove(locationId)
                // 인덱스 확인
                withContext(Dispatchers.Main) { adapter.notifyItemRemoved(position + 2)}
            }
            .onFailure { LocalLogger.e(it) }
    }

    private fun onModifiedResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            contents.removeAll { it is FriendTagAdapter.Content }
            loadContents()
        }
    }
}