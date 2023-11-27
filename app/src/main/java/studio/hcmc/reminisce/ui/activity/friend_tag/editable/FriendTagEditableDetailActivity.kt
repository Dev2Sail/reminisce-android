package studio.hcmc.reminisce.ui.activity.friend_tag.editable

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
import studio.hcmc.reminisce.databinding.ActivityFriendTagEditableDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class FriendTagEditableDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendTagEditableDetailBinding
    private lateinit var adapter: FriendTagEditableAdapter
    private lateinit var locations: List<LocationVO>

    private val opponentId by lazy { intent.getIntExtra("opponentId", -1) }
    private val nickname by lazy { intent.getStringExtra("nickname") }

    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<FriendTagEditableAdapter.Content>()
    private val selectedIds = HashSet<Int /* locationId */>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendTagEditableDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.friendTagEditableDetailAppbar.appbarTitle.text = nickname
        viewBinding.friendTagEditableDetailAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.friendTagEditableDetailAppbar.appbarActionButton1.text = getString(R.string.dialog_remove)
        viewBinding.friendTagEditableDetailAppbar.appbarActionButton1.setOnClickListener { patchContents(selectedIds) }
        loadContents()
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendTagEditableDetailActivity)
        val result = runCatching { LocationIO.listByUserIdAndOpponentId(user.id, opponentId, Int.MAX_VALUE) }
            .onSuccess {it ->
                locations = it
                it.forEach {
                    tagInfo[it.id] = TagIO.listByLocationId(it.id)
                    friendInfo[it.id] = FriendIO.listByUserIdAndLocationId(user.id, it.id)
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        } else {
            CommonError.onMessageDialog(this@FriendTagEditableDetailActivity, getString(R.string.dialog_error_common_list_body))
        }
    }

    private fun prepareContents() {
        for (location in locations.sortedByDescending { it.id }) {
            contents.add(FriendTagEditableAdapter.DetailContent(location, tagInfo[location.id].orEmpty(), friendInfo[location.id].orEmpty()))
        }
    }

    private fun onContentsReady() {
        viewBinding.friendTagEditableItems.layoutManager = LinearLayoutManager(this)
        adapter = FriendTagEditableAdapter(adapterDelegate, summaryDelegate)
        viewBinding.friendTagEditableItems.adapter = adapter
    }

    private val adapterDelegate = object : FriendTagEditableAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val summaryDelegate = object : SummaryViewHolder.Delegate {
        override fun onItemClick(locationId: Int): Boolean {
            if (!selectedIds.add(locationId)) {
                selectedIds.remove(locationId)

                return false
            }

            return true
        }
    }

    private fun patchContents(locationIds: HashSet<Int>) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { locationIds.forEach { LocationIO.delete(it) } }
            .onSuccess {
                Intent().putExtra("isModified", true).setActivity(this@FriendTagEditableDetailActivity, Activity.RESULT_OK)
                finish()
            }.onFailure { LocalLogger.e(it) }
    }
}