package studio.hcmc.reminisce.ui.activity.writer.options

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteSelectFriendBinding
import studio.hcmc.reminisce.dto.location.LocationFriendDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationFriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class WriteOptionFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteSelectFriendBinding
    private lateinit var friends: List<FriendVO>
    private val locationId by lazy { intent.getIntExtra("locationId", -1) }

    private val users = HashMap<Int /* userId */, UserVO>()
    private val selectedFriendIds = HashSet<Int>()
    private val contents = ArrayList<WriteOptionsFriendAdapter.Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteSelectFriendBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.writeSelectFriendAppbar.appbarTitle.text = getText(R.string.card_home_tag_friend_title)
        viewBinding.writeSelectFriendAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.writeSelectFriendAppbar.appbarActionButton1.setOnClickListener {
            val dto = LocationFriendDTO.Post().apply {
                this.locationId = this@WriteOptionFriendActivity.locationId
                this.opponentIds = this@WriteOptionFriendActivity.selectedFriendIds.toList()
            }
            fetchContents(dto)
        }

        prepareFriends()
        prepareSelectedFriends()
    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionFriendActivity)
        val result = runCatching { FriendIO.listByUserId(user.id) }
            .onSuccess {
                friends = it
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

    private fun prepareSelectedFriends() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationFriendIO.listByLocationId(locationId) }
            .onSuccess {
                for (vo in it) {
                    selectedFriendIds.add(vo.opponentId)
                }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun prepareContents() {
        for (friend in friends) {
            contents.add(WriteOptionsFriendAdapter.DetailContent(
                friend.opponentId, friend.nickname ?: users[friend.opponentId]!!.nickname
            ))
        }
    }

    private fun onContentsReady() {
        viewBinding.writeSelectFriendItems.layoutManager = LinearLayoutManager(this)
        viewBinding.writeSelectFriendItems.adapter = WriteOptionsFriendAdapter(adapterDelegate, friendItemDelegate)
    }

    private fun fetchContents(dto: LocationFriendDTO.Post) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationFriendIO.post(dto) }
            .onSuccess {
                // finish?
        }.onFailure { LocalLogger.e(it) }

    }

    private val adapterDelegate = object : WriteOptionsFriendAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val friendItemDelegate = object : WriteOptionFriendItemViewHolder.Delegate {
        override fun onItemClick(opponentId: Int): Boolean {
            if (!selectedFriendIds.add(opponentId)) {
                selectedFriendIds.remove(opponentId)
                return false
            }

            return true
        }
    }
}