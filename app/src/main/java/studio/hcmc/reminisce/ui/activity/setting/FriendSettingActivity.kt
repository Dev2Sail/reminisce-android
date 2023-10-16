package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO
import kotlin.collections.set

class FriendSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingFriendBinding
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

    private fun onContentsReady() {
        viewBinding.settingFriendItems.layoutManager = LinearLayoutManager(this)
        viewBinding.settingFriendItems.adapter = FriendSettingAdapter(friendItemDelegate)
    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendSettingActivity)
        runCatching { FriendIO.listByUserId(user.id) }
            .onSuccess {
                friends = it
                for (friend in it) {
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent
                }

                withContext(Dispatchers.Main) { onContentsReady() }
            }.onFailure {
                Log.v(
                    "reminisce Logger",
                    "[reminisce > Setting > Friend > prepareFriends] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}"
                )
            }
    }

    private fun getFriend(userId: Int): UserVO { return users[userId]!! }

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

    private val friendItemDelegate = object : FriendSettingItemViewHolder.Delegate {
        override val friends: List<FriendVO>
            get() = this@FriendSettingActivity.friends
        override val users: HashMap<Int, UserVO>
            get() = this@FriendSettingActivity.users

        override fun onItemClick(opponentId: Int, friend: FriendVO) {
            UpdateFriendNicknameDialog(
                this@FriendSettingActivity,
                this@FriendSettingActivity,
                friend,
                getFriend(friend.opponentId).nickname,
                getFriend(friend.opponentId).email
            )
        }

        override fun onItemLongClick(opponentId: Int) { DeleteFriendDialog(this@FriendSettingActivity, opponentId) }
        override fun getItemCount() = friends.size
        override fun getItem(position: Int) = friends[position]
    }

    private fun launchSearchFriend() {
        Intent(this, AddFriendActivity::class.java).apply {
            startActivity(this)
        }
    }
}