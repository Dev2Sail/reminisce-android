package studio.hcmc.reminisce.ui.activity.writer.options

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteSelectFriendBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class WriteOptionSelectFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteSelectFriendBinding
    private lateinit var friends: List<FriendVO>
    private val users = HashMap<Int /* userId */, UserVO>()
    private val selectedFriendIds = HashSet<Int>()

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
                // TODO location_friend add
                Intent(this@WriteOptionSelectFriendActivity, WriteActivity::class.java).apply {

//                    putIntegerArrayListExtra("selectedFriendIdList", selectedFriendIds)
//                    putStringArrayListExtra("selectedFriendNicknameList", selectedFriendNicknames)
                    startActivity(this)
                }
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.writeSelectFriendItems.layoutManager = LinearLayoutManager(this)
        viewBinding.writeSelectFriendItems.adapter = WriteOptionsSelectFriendAdapter(friendItemDelegate)
    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionSelectFriendActivity)
        runCatching { FriendIO.listByUserId(user.id) }
            .onSuccess {
                friends = it
                for (friend in it) {
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent
                }

                withContext(Dispatchers.Main) { onContentsReady() }
            }
            .onFailure { LocalLogger.e(it) }
    }

    private val friendItemDelegate = object : WriteOptionSelectFriendItemViewHolder.Delegate {
        override val friends: List<FriendVO>
            get() = this@WriteOptionSelectFriendActivity.friends
        override val users: HashMap<Int, UserVO>
            get() = this@WriteOptionSelectFriendActivity.users

        override fun onItemClick(opponentId: Int): Boolean {
            if (!selectedFriendIds.add(opponentId)) {
                selectedFriendIds.remove(opponentId)
                return false
            }

            return true
        }

        override fun getItemCount() = friends.size
        override fun getItem(position: Int) = friends[position]
    }
}