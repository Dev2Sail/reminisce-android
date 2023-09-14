package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingFriendBinding
import studio.hcmc.reminisce.databinding.LayoutSettingFriendItemBinding
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
        navController()
        prepareFriends()
    }

    private fun initView() {
        viewBinding.settingFriendAppbar.apply {
            appbarTitle.text = getText(R.string.setting_friend)
            appbarActionButton1.isVisible = false
            appbarBack.setOnClickListener { finish() }
        }

        viewBinding.settingFriendAdd.setOnClickListener { launchSearchFriend() }
        viewBinding.settingFriendItems.removeAllViews()

        val menuId = intent.getIntExtra("settingMenuId", -1)
        viewBinding.settingFriendNavView.navItems.selectedItemId = menuId
    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendSettingActivity)
        runCatching { FriendIO.listByUserId(user.id) }
            .onSuccess {
                friends = it
                for (friend in it) {
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent

                    withContext(Dispatchers.Main) { addFriendView(friend)}
                }
            }
            .onFailure {
                Log.v("reminisce Logger", "[reminisce > friendSetting] : msg - ${it.message} ::  localMsg - ${it.localizedMessage} :: cause - ${it.cause}")
            }
    }

    private fun getFriend(userId: Int): UserVO { return users[userId]!! }

    private fun addFriendView(friend: FriendVO) {
        val cardView = LayoutSettingFriendItemBinding.inflate(layoutInflater).apply {
            settingFriendTitle.text = friend.nickname ?: getFriend(friend.opponentId).nickname
            settingFriendItemIcon.setOnClickListener {
                UpdateFriendNicknameDialog(
                    this@FriendSettingActivity,
                    this@FriendSettingActivity,
                    friend,
                    getFriend(friend.opponentId).nickname,
                    getFriend(friend.opponentId).email
                )
            }
        }

        cardView.root.setOnLongClickListener {
            DeleteFriendDialog(this, friend.opponentId)

            false
        }

        viewBinding.settingFriendItems.addView(cardView.root)
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