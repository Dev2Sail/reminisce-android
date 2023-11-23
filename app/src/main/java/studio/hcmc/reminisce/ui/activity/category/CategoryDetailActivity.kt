package studio.hcmc.reminisce.ui.activity.category

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

    private val categoryEditableLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onModifiedResult)
    private val writeByCategoryIdLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onWriteResult)

    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val position by lazy { intent.getIntExtra("position", -1) }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<CategoryDetailAdapter.Content>()
    private val visitInfo = HashMap<Int /* locationId */, Count>()

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
        viewBinding.categoryDetailAddButton.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
                .putExtra("categoryId", categoryId)
            writeByCategoryIdLauncher.launch(intent)
        }
        loadContents()
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryDetailActivity)
        category = CategoryIO.getById(categoryId)
        val fetch: suspend () -> List<LocationVO>
        if (category.title == "Default") {
            fetch = { LocationIO.listByUserId(user.id) }
        } else {
            fetch = { LocationIO.listByCategoryId(categoryId) }
        }

        val result = runCatching { fetch() }
            .onSuccess {
//                locations.addAll(it)
                for (vo in it) {
                    locations.add(vo)
                    tagInfo[vo.id] = TagIO.listByLocationId(vo.id)
                    friendInfo[vo.id] = FriendIO.listByUserIdAndLocationId(user.id, vo.id)
                }
//                it.forEach {
//                    tagInfo[it.id] = TagIO.listByLocationId(it.id)
//                    friendInfo[it.id] = FriendIO.listByUserIdAndLocationId(user.id, it.id)
//                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        } else { onError() }
    }

    private fun onError() {
        CommonError.onMessageDialog(this@CategoryDetailActivity,  getString(R.string.dialog_error_common_list_body))
    }

    // TODO 지역별 방문 횟수
    private fun prepareVisitInfo() {
        for (location in locations) {
            val city = location.roadAddress.split(" ")[0]
            val province = location.roadAddress.split(" ")[1]
        }
    }

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
        adapter = CategoryDetailAdapter(adapterDelegate, headerDelegate, summaryDelegate)
        viewBinding.categoryDetailItems.adapter = adapter
    }

    private val adapterDelegate = object : CategoryDetailAdapter.Delegate {
        override fun getItemCount() = contents.size
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

    private fun launchEditedHome() {
        Intent()
            .putExtra("isEdited", true)
            .putExtra("categoryId", categoryId)
            .putExtra("position", position)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private val summaryDelegate= object : CategoryDetailSummaryViewHolder.Delegate {
        override fun onItemClick(location: LocationVO) {
            // edit 후 result 받아야 함
            Intent(this@CategoryDetailActivity, WriteDetailActivity::class.java).apply {
                putExtra("locationId", location.id)
                putExtra("title", location.title)
                startActivity(this)
            }
        }

        override fun onItemLongClick(locationId: Int, position: Int) {
            SummaryDeleteDialog(this@CategoryDetailActivity, deleteDialogDelegate, locationId, position)
        }
    }

    private val deleteDialogDelegate = object : SummaryDeleteDialog.Delegate {
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

    private fun onModifiedResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            contents.removeAll {it is CategoryDetailAdapter.Content}
            loadContents()
            viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
        }
    }

    private fun onWriteResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            contents.removeAll {it is CategoryDetailAdapter.Content}
            loadContents()
            viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
        }
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