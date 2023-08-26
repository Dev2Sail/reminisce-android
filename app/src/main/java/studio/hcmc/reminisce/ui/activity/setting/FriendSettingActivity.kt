package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
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
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO
import kotlin.collections.set

class FriendSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingFriendBinding
//    private val friends = ArrayList<FriendVO>()
    private lateinit var friends: List<FriendVO>
    private val users = HashMap<Int /* userId*/, UserVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingFriendBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingFriendAppbar.appbarTitle.text = getText(R.string.setting_friend)
        viewBinding.settingFriendAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingFriendAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.settingFriendAdd.setOnClickListener { launchSearchFriend() }
        viewBinding.settingFriendItems.removeAllViews()

        prepareFriends()
    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendSettingActivity)
        val userId = UserIO.getByEmail(user.email).id
        runCatching { FriendIO.listByUserId(userId) }
            .onSuccess {
                friends = it

                for (friend in friends) {
                    if (friend.nickname == null) {
                        val opponent = UserIO.getById(friend.opponentId)
                        users[opponent.id] = opponent
                    }
                    withContext(Dispatchers.Main) { addFriendView(friend)}
                }
            }
            .onFailure {
                it.cause
                it.message
                it.stackTrace
            }
    }

    private fun getFriend(userId: Int): UserVO { return users[userId]!! }

    private fun addFriendView(friend: FriendVO) {
        val cardView = LayoutSettingFriendItemBinding.inflate(layoutInflater)
        cardView.settingFriendTitle.text = friend.nickname ?: getFriend(friend.opponentId).nickname
        cardView.settingFriendItemIcon.setOnClickListener {
            UpdateFriendNicknameDialog(
                this,
                this,
                friend,
                getFriend(friend.opponentId).nickname,
                getFriend(friend.opponentId).email
            )
        }
        cardView.root.setOnLongClickListener {
            DeleteFriendDialog(this, friend.opponentId)

            false
        }
        viewBinding.settingFriendItems.addView(cardView.root)
    }

    private fun launchSearchFriend() {
        Intent(this, AddFriendActivity::class.java).apply {
            startActivity(this)
        }
    }
}