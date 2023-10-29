package studio.hcmc.reminisce.ui.activity.tag

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityTagDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.tag.editable.TagEditableDetailActivity
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class TagDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTagDetailBinding
    private lateinit var adapter: TagDetailAdapter
    private lateinit var locations: List<LocationVO>
    private lateinit var tag: TagVO

    private val tagId by lazy { intent.getIntExtra("tagId", -1) }

    private val users = HashMap<Int /* UserId */, UserVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<TagDetailAdapter.Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTagDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.tagDetailAppbar.apply {
            appbarTitle.text = getString(R.string.header_view_holder_title)
            appbarActionButton1.isVisible = false
            appbarBack.setOnClickListener { finish() }
        }
        prepareTag()

    }

    private fun prepareTag() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@TagDetailActivity)
        val result = runCatching { TagIO.getByUserIdAndTagId(user.id, tagId) }
            .onSuccess {
                tag = it
                loadContents()
            }.onFailure { LocalLogger.e(it)}
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@TagDetailActivity)
        val result = runCatching { LocationIO.listByTagId(tag.id) }
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
            CommonError.onMessageDialog(this@TagDetailActivity, "목록을 불러오는데 실패했어요. \n 다시 실행해 주세요.")
        }
    }

    private fun prepareContents() {
        contents.add(TagDetailAdapter.HeaderContent(tag.body))
        for ((date, locations) in locations.groupBy { it.createdAt.toString().substring(0, 7) }.entries) {
            val (year, month) = date.split("-")
            contents.add(TagDetailAdapter.DateContent(getString(R.string.card_date_separator, year, month.trim('0'))))
            for (location in locations.sortedByDescending { it.id }) {
                contents.add(TagDetailAdapter.DetailContent(
                    location,
                    tagInfo[location.id].orEmpty(),
                    friendInfo[location.id].orEmpty()
                ))
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.tagDetailItems.layoutManager = LinearLayoutManager(this)
        adapter = TagDetailAdapter(
            adapterDelegate,
            headerDelegate,
            summaryDelegate
        )
        viewBinding.tagDetailItems.adapter = adapter
    }

    private val adapterDelegate = object : TagDetailAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : TagDetailHeaderViewHolder.Delegate {
        // TODO editableActivity result
        override fun onEditClick(title: String) {
            Intent(this@TagDetailActivity, TagEditableDetailActivity::class.java).apply {
                putExtra("tagId", tag.id)

                startActivity(this)
            }
        }
    }

    private val summaryDelegate = object : TagDetailSummaryViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String) {
            Intent(this@TagDetailActivity, WriteDetailActivity::class.java).apply {
                putExtra("locationId", locationId)
                putExtra("title", title)
                startActivity(this)
            }
        }

        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }
    }
}
