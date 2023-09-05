package studio.hcmc.reminisce.ui.activity.writer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteSelectFriendBinding
import studio.hcmc.reminisce.databinding.LayoutSelectFriendItemBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class WriteSelectFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteSelectFriendBinding
    private lateinit var friends: List<FriendVO>
    private val users = HashMap<Int /* userId */, UserVO>()
    private val selectedFriends = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteSelectFriendBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
        prepareFriends()

    }

    private fun initView() {
        viewBinding.writeSelectFriendAppbar.apply {
            appbarTitle.text = getText(R.string.card_home_tag_friend_title)
            appbarBack.setOnClickListener { finish() }
        }

    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteSelectFriendActivity)
        val userId = UserIO.getByEmail(user.email).id
        runCatching { FriendIO.listByUserId(userId) }
            .onSuccess {
                friends = it
                for (friend in it) {
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent

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
        val textView = LayoutSelectFriendItemBinding.inflate(layoutInflater)
        textView.apply {
            writeSelectFriendTitle.text = friend.nickname ?: getFriend(friend.opponentId).nickname
            root.setOnClickListener {
                writeSelectFriendIcon.isVisible = true
                selectedFriends.add(friend.opponentId)
            }
        }

        viewBinding.writeSelectFriendItems.addView(textView.root)
    }


}