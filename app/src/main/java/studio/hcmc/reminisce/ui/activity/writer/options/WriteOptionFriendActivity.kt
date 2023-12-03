package studio.hcmc.reminisce.ui.activity.writer.options

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteSelectFriendBinding
import studio.hcmc.reminisce.dto.location.LocationFriendDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationFriendIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class WriteOptionFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteSelectFriendBinding
    private lateinit var adapter: WriteOptionsFriendAdapter
    private lateinit var user: UserVO

    private val context = this
    private val locationId by lazy { intent.getIntExtra("locationId", -1) }

    private val friends = ArrayList<FriendVO>()
    private val checkedOpponentIds = HashMap<Int /* opponentId */, Boolean>()

    private val selectedOpponentIds = HashSet<Int>()
    private val preparePostIds = ArrayList<Int>()
    private val contents = ArrayList<WriteOptionsFriendAdapter.Content>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteSelectFriendBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.writeSelectFriendAppbar.appbarTitle.text = getText(R.string.card_home_tag_friend_title)
        viewBinding.writeSelectFriendAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.writeSelectFriendAppbar.appbarActionButton1.setOnClickListener { patchContents() }
        viewBinding.writeSelectFriendItems.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch { loadContents() }
    }

    private suspend fun prepareUser(): UserVO {
        if (!this::user.isInitialized) {
            user = UserExtension.getUser(this)
        }

        return user
    }

    private suspend fun loadContents() = mutex.withLock {
        val user = prepareUser()
        val lastId = friends.lastOrNull()?.opponentId ?: Int.MAX_VALUE
        val delay = System.currentTimeMillis() - lastLoadedAt - 2000
        if (delay < 0) {
            delay(-delay)
        }

        if (!hasMoreContents) {
            return
        }

        try {
            // userId의 모든 친구 목록
            val fetched = FriendIO.listByUserId(user.id, lastId, false)
            // 해당 location에 등록돼있는 friend
            val saved = FriendIO.listByUserIdAndLocationId(user.id, locationId)
            for (friend in fetched.sortedByDescending { it.opponentId }) {
                friends.add(friend)
                checkedOpponentIds[friend.opponentId] = false
            }
            for (friend in saved) {
                checkedOpponentIds[friend.opponentId] = true
                selectedOpponentIds.add(friend.opponentId)
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

    private fun prepareContents(fetched: List<FriendVO>): Int {
        if (contents.lastOrNull() is WriteOptionsFriendAdapter.ProgressContent) {
            contents.removeLast()
        }

        var size = 0
        for (friend in fetched) {
            val content = WriteOptionsFriendAdapter.DetailContent(friend.opponentId, friend.nickname!!)
            contents.add(content)
        }
        size += fetched.size

        if (hasMoreContents) {
            contents.add(WriteOptionsFriendAdapter.ProgressContent)
        }

        return size
    }

    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            viewBinding.writeSelectFriendItems.adapter = WriteOptionsFriendAdapter(
                adapterDelegate,
                friendItemDelegate
            )
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private fun preparePost(): LocationFriendDTO.Post {
        for (opponent in checkedOpponentIds) {
            if (opponent.value) {
                preparePostIds.add(opponent.key)
            }
        }

        val dto = LocationFriendDTO.Post().apply {
            this.locationId = context.locationId
            this.opponentIds = context.preparePostIds
        }
        return dto
    }

    private fun patchContents() = CoroutineScope(Dispatchers.IO).launch {
        val dto = preparePost()
        val body = ArrayList<String>()
        for (opponent in checkedOpponentIds) {
            if (opponent.value) {
                body.add(prepareBody(opponent.key, friends))
            }
        }
        LocalLogger.v("body ${body.joinToString { it }}")
        runCatching { LocationFriendIO.post(dto) }
            .onSuccess { toOptions(body.joinToString { it }) }
            .onFailure { LocalLogger.e(it) }
    }

    private fun prepareBody(target: Int, friends: List<FriendVO>): String {
        val name = ""
        for (friend in friends) {
            if (friend.opponentId == target) {
                return friend.nickname!!
            }
        }

        return name
    }

    private fun toOptions(body: String) {
        Intent()
            .putExtra("isAdded", true)
            .putExtra("body", body)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private val adapterDelegate = object : WriteOptionsFriendAdapter.Delegate {
        override fun hasMoreContents() = hasMoreContents
        override fun getMoreContents() { CoroutineScope(Dispatchers.IO).launch { loadContents() } }
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val friendItemDelegate = object : WriteOptionFriendItemViewHolder.Delegate {
        override fun onItemClick(opponentId: Int): Boolean {
            if (checkedOpponentIds[opponentId]!!) {
                checkedOpponentIds[opponentId] = false

                return false
            } else {
                checkedOpponentIds[opponentId] = true

                return true
            }
        }

        override fun isChecked(opponentId: Int): Boolean {
            return checkedOpponentIds[opponentId]!!
        }
    }
}