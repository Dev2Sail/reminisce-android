package studio.hcmc.reminisce.ui.activity.tag.editable

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityTagEditableDetailBinding
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
import studio.hcmc.reminisce.vo.user.UserVO

class TagEditableDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTagEditableDetailBinding
    private lateinit var adapter: TagEditableAdapter
    private lateinit var user: UserVO

    private val tagId by lazy { intent.getIntExtra("tagId", -1) }
    private val body by lazy { intent.getStringExtra("tagBody") }

    //friend nullable
    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<TagEditableAdapter.Content>()
    private val selectedIds = HashSet<Int /* locationId */>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTagEditableDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.tagEditableDetailAppbar.appbarTitle.text = body
        viewBinding.tagEditableDetailAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.tagEditableDetailAppbar.appbarActionButton1.text = getString(R.string.dialog_remove)
        viewBinding.tagEditableDetailAppbar.appbarActionButton1.setOnClickListener { patchContents(selectedIds) }
        viewBinding.tagEditableDetailItems.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch { loadContents() }
    }

    private suspend fun prepareUser(): UserVO {
        if(!this::user.isInitialized) {
            user = UserExtension.getUser(this)
        }

        return user
    }

    private suspend fun loadContents() = mutex.withLock {
        val user = prepareUser()
        val lastId = locations.lastOrNull()?.id ?: Int.MAX_VALUE
        val delay = System.currentTimeMillis() - lastLoadedAt - 2000
        if (delay < 0) {
            delay(-delay)
        }

        if (!hasMoreContents) {
            return
        }

        try {
            val fetched = LocationIO.listByTagId(tagId, lastId)
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
        if (contents.lastOrNull() is TagEditableAdapter.ProgressContent) {
            contents.removeLast()
        }

        var size = 0
        for (location in fetched) {
            val content = TagEditableAdapter.DetailContent(
                location,
                tagInfo[location.id].orEmpty(),
                friendInfo[location.id].orEmpty()
            )

            contents.add(content)
        }
        size += fetched.size

        if (hasMoreContents) {
            contents.add(TagEditableAdapter.ProgressContent)
        }

        return size
    }

    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            adapter = TagEditableAdapter(adapterDelegate, summaryDelegate)
            viewBinding.tagEditableDetailItems.adapter = adapter
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private val adapterDelegate = object : TagEditableAdapter.Delegate {
        override fun hasMoreContents() = hasMoreContents
        override fun getMoreContents() { CoroutineScope(Dispatchers.IO).launch { loadContents() } }
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val summaryDelegate = object : ItemViewHolder.Delegate {
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
            .onSuccess { toTagDetail() }
            .onFailure { LocalLogger.e(it) }
    }

    private fun toTagDetail() {
        Intent().putExtra("isModified", true).setActivity(this, Activity.RESULT_OK)
        finish()
    }
}