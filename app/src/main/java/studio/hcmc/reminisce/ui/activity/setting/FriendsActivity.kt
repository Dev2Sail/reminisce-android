package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityFriendsBinding
import studio.hcmc.reminisce.dto.friend.FriendDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO
import kotlin.collections.set

class FriendsActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendsBinding
    private lateinit var adapter: FriendsAdapter
    private lateinit var friends: List<FriendVO>

    private val users = HashMap<Int /* userId */, UserVO>()
    private var contents = ArrayList<FriendsAdapter.Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.friendsNavView, menuId)

        viewBinding.friendsAppbar.apply {
            appbarTitle.text = getText(R.string.setting_friend)
            appbarActionButton1.isVisible = false
            appbarBack.setOnClickListener { finish() }
        }
        viewBinding.friendsSearch.setOnClickListener { launchSearchFriend() }

        onLoadContents()

    }

    private fun onLoadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendsActivity)
        val result = runCatching { FriendIO.listByUserId(user.id) }
            .onSuccess {it ->
                friends = it.sortedBy { it.requestedAt }

                for (friend in it) {
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent
                }
            }.onFailure { LocalLogger.e(it) }

        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        }
    }

    private fun prepareContents() {
        for (friend in friends) {
            contents.add(FriendsAdapter.DetailContent(friend.nickname ?: users[friend.opponentId]!!.nickname))
        }
    }

    private fun onContentsReady() {
        viewBinding.friendsItems.layoutManager = LinearLayoutManager(this)
        adapter = FriendsAdapter(adapterDelegate, itemDelegate)
        viewBinding.friendsItems.adapter = adapter
    }

    private val adapterDelegate = object : FriendsAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val itemDelegate = object : FriendsItemViewHolder.Delegate {
//        override fun onItemClick(opponentId: Int, savedNickname: String?, position: Int) {
//            EditFriendDialog(
//                this@FriendsActivity,
//                opponentId,
//                savedNickname,
//                position,
//                editDialogDelegate
//            )
//        }

        override fun onItemLongClick(opponentId: Int, position: Int) {
            DeleteFriendDialog(this@FriendsActivity, opponentId, position, deleteDialogDelegate)
        }

        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }

        override fun onTestClick(nickname: String, position: Int) {
            val friend = friends[position]
            EditFriendDialog(this@FriendsActivity, friend.opponentId, nickname, position, editDialogDelegate)
        }
    }

    private val editDialogDelegate = object : EditFriendDialog.Delegate {
        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }

        override fun onEditClick(opponentId: Int, body: String?, position: Int) {
            val dto = FriendDTO.Put().apply {
                this.opponentId = opponentId
                this.nickname = body
            }
            onFetchContent(dto, position)
        }
    }

    private fun onFetchContent(dto: FriendDTO.Put, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendsActivity)
        runCatching { FriendIO.put(user.id, dto) }
            .onSuccess {
                withContext(Dispatchers.Main) {
                    contents[position] = FriendsAdapter.DetailContent(dto.nickname ?: users[dto.opponentId]!!.nickname)
                    adapter.notifyItemChanged(position)
                }
            }.onFailure { LocalLogger.e(it) }
    }

    private val deleteDialogDelegate = object : DeleteFriendDialog.Delegate {
        override fun onDeleteClick(opponentId: Int, position: Int) {
            onDeleteContent(opponentId, position)
        }
    }

    private fun onDeleteContent(opponentId: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendsActivity)
        runCatching { FriendIO.delete(user.id, opponentId) }
            .onSuccess {
                withContext(Dispatchers.Main) {
//                    contents[position]
                    adapter.notifyItemRemoved(position)

                }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun launchSearchFriend() {
        Intent(this, AddFriendActivity::class.java).apply {
            startActivity(this)
        }
    }
}