package studio.hcmc.reminisce.ui.activity.category

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCategoryDetailBinding
    private lateinit var adapter: CategoryDetailAdapter
    private lateinit var category: CategoryVO
    /*
    lateinit 확인
    private lateinit var x: Call<T>

    if (this::x.isInitialized) { x.cancel() }
     */

    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val position by lazy { intent.getIntExtra("position", -1) }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<CategoryDetailAdapter.Content>()
//    private var hasNextContents = true
    private var nextContentsSize = 0
    private var preContentsSize = 0

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
        loadContents()
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryDetailActivity)
        category = CategoryIO.getById(categoryId)
        val fetch: suspend () -> List<LocationVO>
        if (category.title == "Default") {
            fetch = { LocationIO.listByUserId(user.id, Int.MAX_VALUE) }
        } else {
            fetch = { LocationIO.listByCategoryId(categoryId, Int.MAX_VALUE) }
        }

        val result = runCatching { fetch() }
            .onSuccess {
                for (vo in it.sortedByDescending { it.id }) {
                    locations.add(vo)
                    tagInfo[vo.id] = TagIO.listByLocationId(vo.id)
                    friendInfo[vo.id] = FriendIO.listByUserIdAndLocationId(user.id, vo.id)
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            LocalLogger.v("sortedByDescending locations first id : ${locations[0].id}")
            prepareContents()
            preContentsSize = contents.size
            withContext(Dispatchers.Main) { onContentsReady() }
        } else { onError() }
    }

    private fun prepareMoreContents() {
        val lastId = locations[locations.size - 1].id
        testMoreContents(lastId)
    }

    private fun testMoreContents(lastId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryDetailActivity)
        val tempLocations = ArrayList<LocationVO>()
        val fetch: suspend () -> List<LocationVO>
        if (category.title == "Default") {
            fetch = { LocationIO.listByUserId(user.id, lastId) }
        } else {
            fetch = { LocationIO.listByCategoryId(category.id, lastId) }
        }
        val result = runCatching { fetch() }
            .onSuccess {
                for (vo in it.sortedByDescending { it.id }) {
                    tempLocations.add(vo)
                    locations.add(vo)
                    friendInfo[vo.id] = FriendIO.listByUserIdAndLocationId(user.id, vo.id)
                    tagInfo[vo.id] = TagIO.listByLocationId(vo.id)
                }
                nextContentsSize = it.size
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            for ((date, locations) in tempLocations.groupBy { it.createdAt.toString().substring(0, 7) }.entries) {
                val (year, month) = date.split("-")
                contents.add(CategoryDetailAdapter.DateContent(getString(R.string.card_date_separator, year, month.trim('0'))))
                for (location in locations) {
                    contents.add(CategoryDetailAdapter.DetailContent(location, tagInfo[location.id].orEmpty(), friendInfo[location.id].orEmpty()))
                }
            }
            withContext(Dispatchers.Main) {
                adapter.notifyItemRangeInserted(preContentsSize, nextContentsSize)
            }
            tempLocations.clear()
            preContentsSize += nextContentsSize

        }
    }

    // moreLoadContents는 async 로 해야 할 듯

    private fun onError() {
        CommonError.onMessageDialog(this@CategoryDetailActivity,  getString(R.string.dialog_error_common_list_body))
    }

    /*

    loadContent() 할 때 moreLoadContents()도 불러와놓고 contents 추가할 준비해둠 -> hasNextContents
    recyclerView 바닥 쳤을 때 hasNextContents true이면 contents에 bottomProgress add && bottomProgress visible true
        -> contents 마지막이 bottom 이면? || bottom이 visible true이면?
        -> 2초 안에 moreLoadContents()에서 준비해둔 contents를 기존 contents에 addAll
        ->
    2초 후 remove bottomProgress -> contents.removeAT(contents.size -1)
    기존 + 새롭게 추가된 contents 로 onContentsReady, adapter에 notify insert range

    moreLoadContents()가 10개 미만이면 hasNextContents false이고 bottomProgress visible false, contents에 bottom not add

     */

    private fun prepareContents() {
        contents.add(CategoryDetailAdapter.HeaderContent(category.title))
        for ((date, locations) in locations.groupBy { it.createdAt.toString().substring(0, 7) }.entries) {
            val (year, month) = date.split("-")
            contents.add(CategoryDetailAdapter.DateContent(getString(R.string.card_date_separator, year, month.trim('0'))))
            for (location in locations.sortedByDescending { it.id }) {
                contents.add(CategoryDetailAdapter.DetailContent(location, tagInfo[location.id].orEmpty(), friendInfo[location.id].orEmpty()))
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.categoryDetailItems.layoutManager = LinearLayoutManager(this)
        adapter = CategoryDetailAdapter(adapterDelegate, headerDelegate, itemDelegate)
        viewBinding.categoryDetailItems.adapter = adapter
    }

    private val adapterDelegate = object : CategoryDetailAdapter.Delegate {
//        override fun getMoreContents() {
//            if (hasMoreContents()) {
//                val lastId = locations[locations.size - 1].id
//                LocalLogger.v("locations last id :$lastId")
////                moreLoadContents(lastId, true)
//            }
//        }

        override fun getMoreContents() {
            nextContentsSize = 0
            prepareMoreContents()
        }
        override fun hasMoreContents(): Boolean {
            // true이면 bottomProgress isVisible true
            val handler = Handler(Looper.getMainLooper())

            if (nextContentsSize < 10) {
                return false
            }

//            return true
            return handler.postDelayed({ true }, 3000)
        }

//        override fun getItemCount() = contents.size
        override fun getItemCount(): Int {
            LocalLogger.v("now contents Size:${contents.size}")
            return contents.size
        }
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : CategoryDetailHeaderViewHolder.Delegate {
        override fun onItemClick() {
            val intent = Intent(this@CategoryDetailActivity, CategoryEditableDetailActivity::class.java)
                .putExtra("categoryId", categoryId)
                .putExtra("categoryTitle", category.title)
            categoryEditableLauncher.launch(intent)
        }

        override fun onTitleEditClick() {
            CategoryTitleEditDialog(this@CategoryDetailActivity, dialogDelegate)
        }
    }

    private val dialogDelegate = object : CategoryTitleEditDialog.Delegate {
        override fun onSaveClick(editedTitle: String) {
            onPatchTitle(editedTitle)
        }
    }

    private fun onPatchTitle(body: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryDetailActivity)
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
            ItemDeleteDialog(this@CategoryDetailActivity, deleteDialogDelegate, locationId, position)
        }
    }

    private val deleteDialogDelegate = object : ItemDeleteDialog.Delegate {
        override fun onClick(locationId: Int, position: Int) {
            var locationIdx = -1
            for (item in locations.withIndex()) {
                if (item.value.id == locationId) {
                    locationIdx = item.index
                }
            }
            LocalLogger.v("locationId:$locationId, position: $position, locationIdx:$locationIdx")
            deleteContent(locationId, position, locationIdx)
        }
    }

    private fun deleteContent(locationId: Int, position: Int, locationIdx: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.delete(locationId) }
            .onSuccess {
                locations.removeAt(locationIdx)
                tagInfo.remove(locationId)
                friendInfo.remove(locationId)
                contents.removeAt(position)
                withContext(Dispatchers.Main) { adapter.notifyItemRemoved(position) }
                viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun onModifiedCategoryEditableResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            contents.removeAll { it is CategoryDetailAdapter.Content }
            loadContents()
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
            val locationId = activityResult.data?.getIntExtra("locationId", -1)
//            if (contents.size == 1) {
//                LocalLogger.v("current contents size: ${contents.size}")
//                loadContents()
//            } else if (contents.size > 2) {
//                LocalLogger.v("current contents size: ${contents.size}")
//                addLocation(locationId!!)
//            }


            contents.removeAll { it is CategoryDetailAdapter.Content }
            loadContents()
            // 왜 removeAll? itemChange만 position에 해주면 되는 거 아님?
//            if (contents.size == 1) {
//                // add date divider
//                // add summary
//                // notify inserted(content.size)
//            } else {
//                // notify inserted(2)
//            }



            viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
        }
    }

    private fun addLocation(locationId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryDetailActivity)
        runCatching { LocationIO.getById(locationId) }
            .onSuccess {
                locations.add(it)
                friendInfo[it.id] = FriendIO.listByUserIdAndLocationId(user.id, it.id)
                tagInfo[it.id] = TagIO.listByLocationId(it.id)
                withContext(Dispatchers.Main) { adapter.notifyItemInserted(contents.size - locations.size) }
            }.onFailure { LocalLogger.e(it) }
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
            var locationIdx = -1
            for (item in locations.withIndex()) {
                if (item.value.id == locationId) {
                    locationIdx = item.index
                }
            }
            patchLocation(locationId!!, position!!, locationIdx, categoryId!!)
        }
    }

    private fun patchLocation(locationId: Int, position: Int, locationIdx: Int, categoryId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryDetailActivity)
        runCatching { LocationIO.getById(locationId) }
            .onSuccess {
                locations[locationIdx] = it
                friendInfo[it.id] = FriendIO.listByUserIdAndLocationId(user.id, it.id)
                tagInfo[it.id] = TagIO.listByLocationId(it.id)
                contents[position] = CategoryDetailAdapter.DetailContent(it, tagInfo[it.id].orEmpty(), friendInfo[it.id].orEmpty())
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
}