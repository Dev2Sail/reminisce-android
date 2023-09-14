package studio.hcmc.reminisce.ui.activity.writer

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
import studio.hcmc.reminisce.databinding.ActivityWriteSelectFriendBinding
import studio.hcmc.reminisce.databinding.LayoutSelectFriendItemBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class WriteOptionsSelectFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteSelectFriendBinding
    private lateinit var friends: List<FriendVO>
    private val users = HashMap<Int /* userId */, UserVO>()
    private val selectedFriendIds = ArrayList<Int>()
    private val selectedFriendNicknames = ArrayList<String>()

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
            appbarActionButton1.setOnClickListener {
                Intent(this@WriteOptionsSelectFriendActivity, WriteActivity::class.java).apply {
                    putIntegerArrayListExtra("selectedFriendIdList", selectedFriendIds)
                    putStringArrayListExtra("selectedFriendNicknameList", selectedFriendNicknames)
                    startActivity(this)
                }
            }
        }
    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionsSelectFriendActivity)
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
                Log.v("reminisce Logger", "[reminisce > writeOptions > friend] : msg - ${it.message} ::  localMsg - ${it.localizedMessage} :: cause - ${it.cause}")
            }
    }

    private fun getFriend(userId: Int): UserVO { return users[userId]!! }

    private fun addFriendView(friend: FriendVO) {
        val textView = LayoutSelectFriendItemBinding.inflate(layoutInflater)
        var checkFlag = false;

        textView.apply {
            writeSelectFriendTitle.text = friend.nickname ?: getFriend(friend.opponentId).nickname
            root.setOnClickListener {
                if (!checkFlag) {
                    writeSelectFriendIcon.isVisible = true
                    selectedFriendIds.add(friend.opponentId)
                    selectedFriendNicknames.add(writeSelectFriendTitle.text.toString())
                    checkFlag = true
                } else {
                    writeSelectFriendIcon.isVisible = false
                    selectedFriendIds.remove(friend.opponentId)
                    selectedFriendNicknames.remove(writeSelectFriendTitle.text.toString())
                    checkFlag = false
                }
            }
        }

        viewBinding.writeSelectFriendItems.addView(textView.root)
    }
}