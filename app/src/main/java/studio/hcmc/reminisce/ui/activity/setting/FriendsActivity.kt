package studio.hcmc.reminisce.ui.activity.setting

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
import studio.hcmc.reminisce.databinding.ActivityFriendsBinding
import studio.hcmc.reminisce.dto.friend.FriendDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class FriendsActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendsBinding
    private lateinit var adapter: FriendsAdapter

    private val context = this
    private val friends = ArrayList<FriendVO>()
    private val users = HashMap<Int /* userId */, UserVO>()
    private var contents = ArrayList<FriendsAdapter.Content>()

    private val addFriendLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onAddFriendResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.friendsNavView, menuId)
        viewBinding.friendsAppbar.appbarTitle.text = getText(R.string.setting_friend)
        viewBinding.friendsAppbar.appbarActionButton1.isVisible = false
        viewBinding.friendsAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.friendsSearch.setOnClickListener { launchAddFriend() }
        loadContents()
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        val result = runCatching { FriendIO.listByUserId(user.id, Int.MAX_VALUE, false) }
            .onSuccess { it ->
                for (friend in it.sortedBy { it.nickname }) {
                    friends.add(friend)
                    users[friend.opponentId] = UserIO.getById(friend.opponentId)
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        }
    }

    private fun loadMoreContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        val lastId = friends.sortedByDescending { it.opponentId }[0].opponentId

    }

    private fun prepareContents() {
        friends.forEach { contents.add(FriendsAdapter.DetailContent(it)) }
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
        // to EditFriendDialog
        override fun onItemClick(friend: FriendVO, position: Int) {
            EditFriendDialog(context, friend, position, editDialogDelegate)
        }

        // to DeleteFriendDialog
        override fun onItemLongClick(opponentId: Int, position: Int) {
            DeleteFriendDialog(context, opponentId, position, deleteDialogDelegate)
        }
    }

    // editDialog
    private val editDialogDelegate = object : EditFriendDialog.Delegate {
        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }

        override fun onEditClick(opponentId: Int, body: String?, position: Int) {
            preparePatch(opponentId, body, position)
        }
    }

    private fun preparePatch(opponentId: Int, body: String?, position: Int) {
        val dto = FriendDTO.Put().apply {
            this.opponentId = opponentId
            this.nickname = body
        }
        onPatchFriend(dto, position)
    }

    private fun onPatchFriend(dto: FriendDTO.Put, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        runCatching { FriendIO.put(user.id, dto) }
            .onSuccess {
                val patch = FriendIO.getByUserIdAndOpponentId(user.id, dto.opponentId)
                LocalLogger.v("patch result: ${patch.opponentId}, ${patch.nickname}")
                friends[position] = patch
                contents[position] = FriendsAdapter.DetailContent(patch)
                withContext(Dispatchers.Main) { adapter.notifyItemChanged(position) }
            }.onFailure { LocalLogger.e(it) }
    }

    // delete dialog
    private val deleteDialogDelegate = object : DeleteFriendDialog.Delegate {
        override fun onDeleteClick(opponentId: Int, position: Int) {
            onDeleteFriend(opponentId, position)
        }
    }

    private fun onDeleteFriend(opponentId: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        runCatching { FriendIO.delete(user.id, opponentId) }
            .onSuccess {
                contents.removeAt(position)
                users.remove(opponentId)
                friends.removeAt(position)
                withContext(Dispatchers.Main) { adapter.notifyItemRemoved(position) }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun launchAddFriend() {
        val intent = Intent(this, AddFriendActivity::class.java)
        addFriendLauncher.launch(intent)
    }

    private fun onAddFriendResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            val opponentId = activityResult.data?.getIntExtra("opponentId", -1)
            onAddContent(opponentId!!)
        }
    }

    private fun onAddContent(opponentId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        runCatching { FriendIO.getByUserIdAndOpponentId(user.id, opponentId) }
            .onSuccess {
                friends.add(it)
                users[it.opponentId] = UserIO.getById(it.opponentId)
                contents.add(FriendsAdapter.DetailContent(it))
                withContext(Dispatchers.Main) { adapter.notifyItemInserted(friends.size) }
            }.onFailure { LocalLogger.e(it) }
    }
}