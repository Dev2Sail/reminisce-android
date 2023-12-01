package studio.hcmc.reminisce.ui.activity.writer.options

import android.app.Activity
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
import studio.hcmc.reminisce.dto.location.LocationFriendDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationFriendIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.friend.FriendVO

class WriteOptionFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteSelectFriendBinding
    private lateinit var friends: List<FriendVO>

    private val context = this
    private val locationId by lazy { intent.getIntExtra("locationId", -1) }

    private val selectedFriendIds = HashSet<Int>()
    private val preparePostIds = ArrayList<Int>()
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
        viewBinding.writeSelectFriendAppbar.appbarActionButton1.setOnClickListener { patchContents() }
        prepareFriends()
        prepareSavedFriends()
    }

    private fun prepareFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        val result = runCatching { FriendIO.listByUserId(user.id, Int.MAX_VALUE,false) }
            .onSuccess { friends = it }
            .onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        }
    }

    private fun prepareSavedFriends() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        runCatching { FriendIO.listByUserIdAndLocationId(user.id, locationId) }
            .onSuccess {
                for (vo in it) {
                    selectedFriendIds.add(vo.opponentId)
                }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun prepareContents() {
        for (friend in friends) {
            contents.add(WriteOptionsFriendAdapter.DetailContent(friend.opponentId, friend.nickname!!))
        }
    }

    private fun onContentsReady() {
        viewBinding.writeSelectFriendItems.layoutManager = LinearLayoutManager(this)
        viewBinding.writeSelectFriendItems.adapter = WriteOptionsFriendAdapter(adapterDelegate, friendItemDelegate)
    }

    private fun preparePost(): LocationFriendDTO.Post {
        for (id in selectedFriendIds) {
            preparePostIds.add(id)
        }
        val dto = LocationFriendDTO.Post().apply {
            this.locationId = context.locationId
            this.opponentIds = context.preparePostIds
        }
        return dto
    }

    private fun patchContents() = CoroutineScope(Dispatchers.IO).launch {
        val dto = preparePost()
        val body = ArrayList<String>()
        for (friend in friends) {
            for (id in selectedFriendIds) {
                if (friend.opponentId == id) {
                    body.add(friend.nickname!!)
                }
            }
        }
        LocalLogger.v("body ${body.joinToString { it }}")
        runCatching { LocationFriendIO.post(dto) }
            .onSuccess { toOptions(body.joinToString { it }) }
            .onFailure { LocalLogger.e(it) }
    }

    private fun toOptions(body: String) {
        Intent()
            .putExtra("isAdded", true)
            .putExtra("body", body)
            .setActivity(this, Activity.RESULT_OK)
        finish()
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

        override fun validate(opponentId: Int): Boolean {
            return selectedFriendIds.contains(opponentId)
        }
    }
}
// TODO 기존에 선택돼있던 친구 삭제가 안 됨