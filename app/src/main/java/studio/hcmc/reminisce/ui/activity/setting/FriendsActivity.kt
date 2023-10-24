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
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.friendsNavView, menuId)

        viewBinding.apply {
            friendsAppbar.apply {
                appbarTitle.text = getText(R.string.setting_friend)
                appbarActionButton1.isVisible = false
                appbarBack.setOnClickListener { finish() }
            }
            friendsSearch.setOnClickListener { launchSearchFriend() }
        }

        prepareFriends()
    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendsActivity)
        runCatching { FriendIO.listByUserId(user.id) }
            .onSuccess {it ->
                friends = it.sortedBy { it.requestedAt }
                for (friend in it) {
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent
                }

                withContext(Dispatchers.Main) { onContentsReady() }
            }.onFailure {
                LocalLogger.e(it)
            }
    }

    private fun onContentsReady() {
        viewBinding.friendsItems.layoutManager = LinearLayoutManager(this)
        adapter = FriendsAdapter(adapterDelegate, itemDelegate)
        viewBinding.friendsItems.adapter = adapter
    }

    private val itemDelegate = object : FriendsItemViewHolder.Delegate {
        override fun onItemClick(opponentId: Int, friend: FriendVO) {
            EditFriendDialog(
                this@FriendsActivity,
                this@FriendsActivity,
                friend,
                dialogDelegate
            )
        }

        override fun onItemLongClick(opponentId: Int) {
            DeleteFriendDialog(this@FriendsActivity, opponentId)
        }

        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }
    }

    private val dialogDelegate = object : EditFriendDialog.Delegate {
        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }
    }

    private val adapterDelegate = object : SingleTypeAdapterDelegate<FriendVO> {
        override fun getItemCount() = friends.size
        override fun getItem(position: Int) = friends[position]
    }

    private fun launchSearchFriend() {
        Intent(this, AddFriendActivity::class.java).apply {
            startActivity(this)
        }
    }
}