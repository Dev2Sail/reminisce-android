package studio.hcmc.reminisce.ui.activity.category

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import studio.hcmc.reminisce.databinding.ActivityCategoryDetailBinding
import studio.hcmc.reminisce.dto.category.CategoryDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.category.editable.CategoryEditableDetailActivity
import studio.hcmc.reminisce.ui.activity.category.editable.CategoryTitleEditDialog
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCategoryDetailBinding
    private lateinit var adapter: CategoryDetailAdapter
    private lateinit var user: UserVO
    private lateinit var category: CategoryVO

    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val position by lazy { intent.getIntExtra("position", -1) }

    private val context = this
    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<CategoryDetailAdapter.Content>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    private val categoryEditableLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onModifiedCategoryEditableResult)
    private val editWriteLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onEditWriteResult)
    private val addWriteLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onWriteResult)

    private data class Count(
        val city: String,
        val province: String,
        val count: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.categoryDetailAppbar.appbarTitle.text = getText(R.string.header_view_holder_title)
        viewBinding.categoryDetailAppbar.appbarActionButton1.isVisible = false
        viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.categoryDetailAddButton.setOnClickListener { launchWrite() }
        viewBinding.categoryDetailItems.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch { loadContents() }
    }

    private suspend fun prepareUser(): UserVO {
        if (!this::user.isInitialized) {
            user = UserExtension.getUser(context)
        }

        return user
    }

    private suspend fun prepareCategory(): CategoryVO {
        if (!this::category.isInitialized) {
            category = CategoryIO.getById(categoryId)
        }

        return category
    }

    private suspend fun fetch(): List<LocationVO> {
        val user = prepareUser()
        val category = prepareCategory()
        val lastId = locations.lastOrNull()?.id ?: Int.MAX_VALUE
        if (category.title == "Default") {
            return LocationIO.listByUserId(user.id, lastId)
        } else {
            return LocationIO.listByCategoryId(category.id, lastId)
        }
    }

    private suspend fun loadContents() = mutex.withLock {
        val delay = System.currentTimeMillis() - lastLoadedAt - 2000
        Log.v("delay", "${delay}ms")
        if (delay < 0) {
            delay(-delay)
        }

        if (!hasMoreContents) {
            return
        }

        try {
            val fetched = fetch().sortedByDescending { it.id }
            for (location in fetched) {
                this@CategoryDetailActivity.locations.add(location)
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
        CommonError.onMessageDialog(this,  getString(R.string.dialog_error_common_list_body))
    }

    private fun prepareContents(fetched: List<LocationVO>): Int {
        if (contents.lastOrNull() is CategoryDetailAdapter.ProgressContent) {
            contents.removeLast()
        }

        if (contents.isEmpty()) {
            contents.add(CategoryDetailAdapter.HeaderContent(category.title))
        }

        val group = fetched.groupByTo(HashMap()) { it.createdAt.toString().substring(0, 7) }
        var size = 0
        val lastDetailContent = contents.lastOrNull() as? CategoryDetailAdapter.DetailContent
        if (lastDetailContent != null) {
            val list = group.remove(lastDetailContent.location.createdAt.toString().substring(0, 7))
            if (list != null) {
                size += list.size
                addDetailContents(list)
            }
        }

        for ((date, locations) in group) {
            val (year, month) = date.split("-")
            size++
            contents.add(CategoryDetailAdapter.DateContent(getString(R.string.card_date_separator, year, month.removePrefix("0"))))
            size += locations.size
            addDetailContents(locations)
        }

        if (hasMoreContents) {
            contents.add(CategoryDetailAdapter.ProgressContent)
        }

        return size
    }

    private fun addDetailContents(locations: List<LocationVO>) {
        for (location in locations) {
            val content = CategoryDetailAdapter.DetailContent(
                location = location,
                tags = tagInfo[location.id].orEmpty(),
                friends = friendInfo[location.id].orEmpty()
            )

            contents.add(content)
        }
    }

    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            adapter = CategoryDetailAdapter(adapterDelegate, headerDelegate, itemDelegate)
            viewBinding.categoryDetailItems.adapter = adapter
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private val adapterDelegate = object : CategoryDetailAdapter.Delegate {
        override fun hasMoreContents() = hasMoreContents
        override fun getMoreContents() { CoroutineScope(Dispatchers.IO).launch { loadContents() } }
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : CategoryDetailHeaderViewHolder.Delegate {
        override fun onItemClick() {
            val intent = Intent(context, CategoryEditableDetailActivity::class.java)
                .putExtra("categoryId", categoryId)
                .putExtra("categoryTitle", category.title)
            categoryEditableLauncher.launch(intent)
        }

        override fun onTitleEditClick() {
            CategoryTitleEditDialog(context, dialogDelegate)
        }
    }

    private val dialogDelegate = object : CategoryTitleEditDialog.Delegate {
        override fun onSaveClick(editedTitle: String) {
            onPatchTitle(editedTitle)
        }
    }

    private fun onPatchTitle(body: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        val dto = CategoryDTO.Patch().apply {
            title = body
        }
        val result = runCatching { CategoryIO.patch(user.id, categoryId, dto) }
            .onSuccess {
                withContext(Dispatchers.Main) {
                    contents[0] = CategoryDetailAdapter.HeaderContent(body)
                    adapter.notifyItemChanged(0)
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { launchEditedHome() }
        }
    }

    private val itemDelegate= object : CategoryDetailItemViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String, position: Int) {
            LocalLogger.v("summary click : locationId:$locationId, title:$title, position:$position")
            launchEditWrite(locationId, title, position)
        }

        override fun onItemLongClick(locationId: Int, position: Int) {
            ItemDeleteDialog(context, deleteDialogDelegate, locationId, position)
        }
    }

    private val deleteDialogDelegate = object : ItemDeleteDialog.Delegate {
        override fun onClick(locationId: Int, position: Int) {
            LocalLogger.v("locationId:$locationId, position: $position, locationIdx:${findIndexInList(locationId, locations)}")
            deleteContent(locationId, position, findIndexInList(locationId, locations))
        }
    }

    private fun deleteContent(locationId: Int, position: Int, locationIndex: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.delete(locationId) }
            .onSuccess {
                locations.removeAt(locationIndex)
                tagInfo.remove(locationId)
                friendInfo.remove(locationId)
                contents.removeAt(position)
                withContext(Dispatchers.Main) { adapter.notifyItemRemoved(position) }
                viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun onModifiedCategoryEditableResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            contents.clear()
            CoroutineScope(Dispatchers.IO).launch { loadContents() }
            viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
        }
    }

    private fun launchWrite() {
        val intent = Intent(this, WriteActivity::class.java)
            .putExtra("categoryId", categoryId)
        addWriteLauncher.launch(intent)
    }

    // location이 added
    private fun onWriteResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            contents.clear()
            CoroutineScope(Dispatchers.IO).launch { loadContents() }
            viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
        }
    }

    private fun launchEditWrite(locationId: Int, title: String, position: Int) {
        val intent = Intent(this, WriteDetailActivity::class.java)
            .putExtra("locationId", locationId)
            .putExtra("position", position)
            .putExtra("title", title)
        editWriteLauncher.launch(intent)
    }

    private fun onEditWriteResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            val locationId = activityResult.data?.getIntExtra("locationId", -1)
            val categoryId = activityResult.data?.getIntExtra("categoryId", -1)
            val position = activityResult.data?.getIntExtra("position", -1)
            patchLocation(locationId!!, position!!, findIndexInList(locationId, locations), categoryId!!)
        }
    }

    private fun patchLocation(
        locationId: Int,
        position: Int,
        locationIndex: Int,
        categoryId: Int
    ) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        runCatching { LocationIO.getById(locationId) }
            .onSuccess {
                locations[locationIndex] = it
                friendInfo[it.id] = FriendIO.listByUserIdAndLocationId(user.id, it.id)
                tagInfo[it.id] = TagIO.listByLocationId(it.id)
                val content = CategoryDetailAdapter.DetailContent(
                    it,
                    tagInfo[it.id].orEmpty(),
                    friendInfo[it.id].orEmpty()
                )
                contents[position] = content
                withContext(Dispatchers.Main) {
                    adapter.notifyItemChanged(position)
                    if (categoryId != it.categoryId) {
                        viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
                    }
                }
            }.onFailure { LocalLogger.e(it)}
    }

    private fun launchEditedHome() {
        Intent()
            .putExtra("isEdited", true)
            .putExtra("categoryId", categoryId)
            .putExtra("position", position)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private fun launchModifiedHome() {
        Intent()
            .putExtra("isModified", true)
            .putExtra("categoryId", categoryId)
            .putExtra("position", position)
            .setActivity(this, Activity.RESULT_OK)
        finish()
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