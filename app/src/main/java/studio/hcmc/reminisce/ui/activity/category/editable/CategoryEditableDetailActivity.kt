package studio.hcmc.reminisce.ui.activity.category.editable

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
import studio.hcmc.reminisce.databinding.ActivityCategoryEditableDetailBinding
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

class CategoryEditableDetailActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityCategoryEditableDetailBinding
    private lateinit var adapter: CategoryEditableDetailAdapter
    private lateinit var locations: List<LocationVO>

    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val title by lazy { intent.getStringExtra("categoryTitle") }

    private val users = HashMap<Int /* UserId */, UserVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<CategoryEditableDetailAdapter.Content>()
    private val selectedIds = HashSet<Int>()

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
        val set = HashSet<Int>()
        set.add(1)
        set.add(2)
        val list = set.toMutableList()
        LocalLogger.v("set to list : $list")
        LocalLogger.v("categoryId : $categoryId =================")
        loadContents()
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryEditableDetailActivity)
        val result = runCatching { LocationIO.listByCategoryId(categoryId) }
            .onSuccess {
                locations = it
                for (location in it) {
                    tagInfo[location.id] = TagIO.listByLocationId(location.id)
                    friendInfo[location.id] = FriendIO.listByUserIdAndLocationId(user.id, location.id)
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
        } else { onError() }
    }

    private fun prepareContents() {
        for (location in locations.sortedByDescending { it.id }) {
            contents.add(CategoryEditableDetailAdapter.DetailContent(
                location,
                tagInfo[location.id].orEmpty(),
                friendInfo[location.id].orEmpty()
            ))
        }
    }

    private fun onContentsReady() {
        viewBinding.categoryEditableDetailItems.layoutManager = LinearLayoutManager(this)
        adapter = CategoryEditableDetailAdapter(adapterDelegate, summaryDelegate)
        viewBinding.categoryEditableDetailItems.adapter = adapter
    }

    private fun onError() {
        CommonError.onMessageDialog(this@CategoryEditableDetailActivity, getString(R.string.dialog_error_common_list_body))
    }

    private val adapterDelegate = object : CategoryEditableDetailAdapter.Delegate {
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

    private fun preparePatch(ids: HashSet<Int>) {
        for (id in ids) { fetchContents(id) }
    }

    private fun fetchContents(locationId: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.delete(locationId) }
            .onSuccess { launchCategoryEditableDetail() }
            .onFailure { LocalLogger.e(it) }
    }

    private fun launchCategoryEditableDetail() {
        Intent().putExtra("isModified", true).setActivity(this@CategoryEditableDetailActivity, Activity.RESULT_OK)
        finish()
    }
}


