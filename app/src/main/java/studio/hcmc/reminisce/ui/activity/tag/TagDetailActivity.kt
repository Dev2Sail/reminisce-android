package studio.hcmc.reminisce.ui.activity.tag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityTagDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.category.ItemDeleteDialog
import studio.hcmc.reminisce.ui.activity.tag.editable.TagEditableDetailActivity
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class TagDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTagDetailBinding
    private lateinit var adapter: TagDetailAdapter
    private lateinit var tag: TagVO
    private lateinit var user: UserVO

    private val tagId by lazy { intent.getIntExtra("tagId", -1) }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<TagDetailAdapter.Content>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    private val tagEditableLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onModifiedResult)
    private val writeDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onWriteDetailResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTagDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.tagDetailAppbar.appbarTitle.text = getString(R.string.header_view_holder_title)
        viewBinding.tagDetailAppbar.appbarActionButton1.isVisible = false
        viewBinding.tagDetailAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.tagDetailItems.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch { loadContents() }
    }

    private suspend fun prepareUser(): UserVO {
        if (!this::user.isInitialized) {
            user = UserExtension.getUser(this)
        }

        return user
    }

    private suspend fun prepareTag(): TagVO {
        if (!this::tag.isInitialized) {
            tag = TagIO.getById(tagId)
        }

        return tag
    }

    private suspend fun loadContents() = mutex.withLock {
        val user = prepareUser()
        val tag = prepareTag()
        val lastId = locations.lastOrNull()?.id ?: Int.MAX_VALUE
        val delay = System.currentTimeMillis() - lastLoadedAt - 2000
        if (delay < 0) {
            delay(-delay)
        }

        if (!hasMoreContents) {
            return
        }

        try {
            val fetched = LocationIO.listByTagId(tag.id, lastId)
            for (location in fetched.sortedByDescending { it.id }) {
                locations.add(location)
                tagInfo[location.id] = TagIO.listByLocationId(location.id)
                friendInfo[location.id] = FriendIO.listByUserIdAndLocationId(user.id, location.id)
            }
            hasMoreContents = fetched.size >= 10
            val preSize = contents.size
            val size = prepareContents(fetched)
            withContext(Dispatchers.Main) { onContentsReady(preSize, size) }
            lastLoadedAt = System.currentTimeMillis()
        } catch (e: Throwable) {
            LocalLogger.e(e)
            withContext(Dispatchers.Main) { onError() }
        }
    }

    private fun onError() {
        CommonError.onMessageDialog(this, getString(R.string.dialog_error_common_list_body))
    }

    private fun prepareContents(fetched: List<LocationVO>): Int {
        if (contents.lastOrNull() is TagDetailAdapter.ProgressContent) {
            contents.removeLast()
        }

        if (contents.isEmpty()) {
            contents.add(TagDetailAdapter.HeaderContent(tag.body))
        }

        val group = fetched.groupByTo(HashMap()) { it.createdAt.toString().substring(0, 7) }
        var size = 0
        val lastDetailContent = contents.lastOrNull() as? TagDetailAdapter.DetailContent
        if (lastDetailContent != null) {
            val list = group.remove(lastDetailContent.location.createdAt.toString().substring(0, 7))
            if (list != null) {
                size += list.size
                addDetailContent(list)
            }
        }

        for((date, locations) in group) {
            val (year, month) = date.split("-")
            size++
            contents.add(TagDetailAdapter.DateContent(getString(R.string.card_date_separator, year, month.removePrefix("0"))))
            size += locations.size
            addDetailContent(locations)
        }

        if (hasMoreContents) {
            contents.add(TagDetailAdapter.ProgressContent)
        }

        return size
    }

    private fun addDetailContent(locations: List<LocationVO>) {
        for (location in locations) {
            val content = TagDetailAdapter.DetailContent(
                location = location,
                tags = tagInfo[location.id].orEmpty(),
                friends = friendInfo[location.id].orEmpty()
            )

            contents.add(content)
        }
    }

    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            adapter = TagDetailAdapter(adapterDelegate, headerDelegate, summaryDelegate)
            viewBinding.tagDetailItems.adapter = adapter
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private val adapterDelegate = object : TagDetailAdapter.Delegate {
        override fun hasMoreContents() = hasMoreContents
        override fun getMoreContents() { CoroutineScope(Dispatchers.IO).launch { loadContents() } }
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : TagDetailHeaderViewHolder.Delegate {
        override fun onEditClick() { launchTagEditableDetail(tag.id, tag.body) }
    }

    private val summaryDelegate = object : TagDetailItemViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String) {
            launchWriteDetail(locationId, title)
        }
        override fun onItemLongClick(locationId: Int, position: Int) {
            ItemDeleteDialog(this@TagDetailActivity, deleteDialogDelegate, locationId, position)
        }
    }

    private val deleteDialogDelegate = object : ItemDeleteDialog.Delegate {
        override fun onClick(locationId: Int, position: Int) {
            LocalLogger.v("locationId:$locationId, position:$position")
            deleteContent(locationId, position, findIndexInList(locationId, locations))
        }
    }

    private fun deleteContent(locationId: Int, position: Int, locationIndex: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.delete(locationId) }
            .onSuccess {
                locations.removeAt(locationIndex)
                tagInfo.remove(locationId)
                friendInfo.remove(locationId)
                withContext(Dispatchers.Main) { adapter.notifyItemRemoved(position) }
                viewBinding.tagDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun launchModifiedHome() {
        Intent().putExtra("isModified", true).setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private fun launchTagEditableDetail(tagId: Int, body: String) {
        val intent = Intent(this, TagEditableDetailActivity::class.java)
            .putExtra("tagId", tagId)
            .putExtra("tagBody", body)
        tagEditableLauncher.launch(intent)
    }

    private fun onModifiedResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            contents.clear()
            CoroutineScope(Dispatchers.IO).launch { loadContents() }
        }
    }

    private fun launchWriteDetail(locationId: Int, title: String) {
        val intent = Intent(this, WriteDetailActivity::class.java)
            .putExtra("locationId", locationId)
            .putExtra("title", title)
        writeDetailLauncher.launch(intent)
    }

    private fun onWriteDetailResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            val preSize = contents.size
            locations.clear()
            contents.clear()
            hasMoreContents = true
            lastLoadedAt = 0
            adapter.notifyItemRangeRemoved(0, preSize)
            CoroutineScope(Dispatchers.IO).launch { loadContents() }
        }
    }

    private fun findIndexInList(target: Int, list: ArrayList<LocationVO>): Int {
        var finalIndex = -1
        for ((index, location) in list.withIndex()) {
            if (location.id == target) {
                finalIndex = index
            }
        }

        return finalIndex
    }
}
