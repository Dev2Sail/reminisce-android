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
import studio.hcmc.reminisce.databinding.ActivitySettingFriendBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.Navigation
import studio.hcmc.reminisce.ui.view.SingleTypeAdapterDelegate
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO
import kotlin.collections.set

class FriendListActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingFriendBinding
    private lateinit var adapter: FriendListAdapter
    private lateinit var friends: List<FriendVO>
    private val users = HashMap<Int /* userId */, UserVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingFriendBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("settingMenuId", -1)

        viewBinding.apply {
            settingFriendAppbar.apply {
                appbarTitle.text = getText(R.string.setting_friend)
                appbarActionButton1.isVisible = false
                appbarBack.setOnClickListener { finish() }
            }
            settingFriendAdd.setOnClickListener { launchSearchFriend() }
            settingFriendNavView.navItems.selectedItemId = menuId
        }

        prepareFriends()
        navController()
    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendListActivity)
        runCatching { FriendIO.listByUserId(user.id) }
            .onSuccess {
                friends = it
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
        viewBinding.settingFriendItems.layoutManager = LinearLayoutManager(this)
        adapter = FriendListAdapter(adapterDelegate, itemDelegate)
        viewBinding.settingFriendItems.adapter = adapter
    }

    private val itemDelegate = object : FriendListViewHolder.Delegate {
        override fun onItemClick(opponentId: Int, friend: FriendVO) {
            EditFriendDialog(
                this@FriendListActivity,
                this@FriendListActivity,
                friend,
                dialogDelegate
            )
        }

        override fun onItemLongClick(opponentId: Int) {
            DeleteFriendDialog(this@FriendListActivity, opponentId)
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
    private fun navController() {
        viewBinding.settingFriendNavView.navItems.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_main_home -> {
                    startActivity(Navigation.onNextHome(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_map -> {
                    startActivity(Navigation.onNextMap(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_report -> {
                    startActivity(Navigation.onNextReport(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_setting -> {
                    true
                }

                else -> false
            }
        }
    }

    private fun launchSearchFriend() {
        Intent(this, AddFriendActivity::class.java).apply {
            startActivity(this)
        }
    }
}