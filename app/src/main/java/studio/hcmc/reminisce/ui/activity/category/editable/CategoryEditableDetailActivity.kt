package studio.hcmc.reminisce.ui.activity.category.editable

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
import studio.hcmc.reminisce.databinding.ActivityCategoryEditableDetailBinding
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

class CategoryEditableDetailActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityCategoryEditableDetailBinding
    private lateinit var adapter: CategoryEditableDetailAdapter
    private lateinit var user: UserVO

    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val title by lazy { intent.getStringExtra("categoryTitle") }

    private val locations = ArrayList<LocationVO>()
    private val context = this
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<CategoryEditableDetailAdapter.Content>()
    private val selectedIds = HashSet<Int>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCategoryEditableDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.categoryEditableDetailAppbar.appbarTitle.text = when (title) {
            "Default" -> getString(R.string.category_view_holder_title)
            "new" -> getString(R.string.add_category_body)
            else -> title
        }
        viewBinding.categoryEditableDetailAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.categoryEditableDetailAppbar.appbarActionButton1.text = getString(R.string.dialog_remove)
        viewBinding.categoryEditableDetailAppbar.appbarActionButton1.setOnClickListener { preparePatch(selectedIds) }
        viewBinding.categoryEditableDetailItems.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch { loadContents() }
    }

    private suspend fun prepareUser(): UserVO {
        if (!this::user.isInitialized) {
            user = UserExtension.getUser(context)
        }

        return user
    }

    private suspend fun fetch(): List<LocationVO> {
        val user = prepareUser()
        val lastId = locations.lastOrNull()?.id ?: Int.MAX_VALUE
        if (title == "Default") {
            return LocationIO.listByUserId(user.id, lastId)
        } else {
            return LocationIO.listByCategoryId(categoryId, lastId)
        }
    }

    private suspend fun loadContents() = mutex.withLock {
        val delay = System.currentTimeMillis() - lastLoadedAt - 2000
        if (delay < 0) {
            delay(-delay)
        }

        if (!hasMoreContents) {
            return
        }

        try {
            val fetched = fetch().sortedByDescending { it.id }
            for (location in fetched) {
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
        CommonError.onMessageDialog(context, getString(R.string.dialog_error_common_list_body))
    }

    // 해결 안 됨
    private fun prepareContents(fetched: List<LocationVO>): Int {
        if (contents.lastOrNull() is CategoryEditableDetailAdapter.ProgressContent) {
            contents.removeLast()
        }
        var size = 0
        addDetailContents(fetched)
        size += fetched.size

        if (hasMoreContents) {
            contents.add(CategoryEditableDetailAdapter.ProgressContent)
        }

        return size
    }

    private fun addDetailContents(locations: List<LocationVO>) {
        for (location in locations) {
            val content = CategoryEditableDetailAdapter.DetailContent(
                location = location,
                tags = tagInfo[location.id].orEmpty(),
                friends = friendInfo[location.id].orEmpty()
            )

            contents.add(content)
        }
    }

    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            adapter = CategoryEditableDetailAdapter(adapterDelegate, summaryDelegate)
            viewBinding.categoryEditableDetailItems.adapter = adapter
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private val adapterDelegate = object : CategoryEditableDetailAdapter.Delegate {
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

    private fun preparePatch(ids: HashSet<Int>) {
        for (id in ids) { patchContents(id) }
    }

    private fun patchContents(locationId: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.delete(locationId) }
            .onSuccess { toCategoryEditableDetail() }
            .onFailure { LocalLogger.e(it) }
    }

    private fun toCategoryEditableDetail() {
        Intent().putExtra("isModified", true).setActivity(this, Activity.RESULT_OK)
        finish()
    }
}


