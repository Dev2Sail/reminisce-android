package studio.hcmc.reminisce.ui.activity.tag.editable

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
import studio.hcmc.reminisce.databinding.ActivityTagEditableDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class TagEditableDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTagEditableDetailBinding
    private lateinit var adapter: TagEditableAdapter
    private lateinit var locations: List<LocationVO>

    private val tagId by lazy { intent.getIntExtra("tagId", -1) }
    private val body by lazy { intent.getStringExtra("tagBody") }

    private val users = HashMap<Int /* UserId */, UserVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()

    private val contents = ArrayList<TagEditableAdapter.Content>()
    private val selectedIds = HashSet<Int /* locationId */>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTagEditableDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.tagEditableDetailAppbar.apply {
            appbarTitle.text = body
            appbarBack.setOnClickListener { finish() }
            appbarActionButton1.text = getString(R.string.dialog_remove)
            appbarActionButton1.setOnClickListener { fetchContents(selectedIds) }
        }

        loadContents()
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@TagEditableDetailActivity)
        val result = runCatching { LocationIO.listByTagId(tagId) }
            .onSuccess { it ->
                locations = it
                it.forEach {
                    tagInfo[it.id] = TagIO.listByLocationId(it.id)
                    friendInfo[it.id] = FriendIO.listByUserIdAndLocationId(user.id, it.id)
                }

                for (friends in friendInfo.values) {
                    for (friend in friends) {
                        if (friend.nickname == null) {
                            val opponent = UserIO.getById(friend.opponentId)
                            users[opponent.id] = opponent
                        }
                    }
                }
            }.onFailure { LocalLogger.e(it) }

        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        } else {
            CommonError.onMessageDialog(this@TagEditableDetailActivity, getString(R.string.dialog_error_common_list_body))
        }
    }

    private fun prepareContents() {
        for (location in locations.sortedByDescending { it.id }) {
            contents.add(TagEditableAdapter.DetailContent(
                location,
                tagInfo[location.id].orEmpty(),
                friendInfo[location.id].orEmpty()
            ))
        }
    }

    private fun onContentsReady() {
        viewBinding.tagEditableDetailItems.layoutManager = LinearLayoutManager(this)
        adapter = TagEditableAdapter(adapterDelegate, summaryDelegate)
        viewBinding.tagEditableDetailItems.adapter = adapter
    }

    private val adapterDelegate = object : TagEditableAdapter.Delegate {
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

        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }
    }

    private fun fetchContents(locationIds: HashSet<Int>) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { locationIds.forEach { LocationIO.delete(it) } }
            .onSuccess {
                Intent().putExtra("isModified", true).setActivity(this@TagEditableDetailActivity, Activity.RESULT_OK)
                finish()
            }.onFailure { LocalLogger.e(it) }
    }
}