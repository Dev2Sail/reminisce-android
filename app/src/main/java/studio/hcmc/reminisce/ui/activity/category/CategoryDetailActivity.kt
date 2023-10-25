package studio.hcmc.reminisce.ui.activity.category

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
import studio.hcmc.reminisce.databinding.ActivityCategoryDetailBinding
import studio.hcmc.reminisce.dto.category.CategoryDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.category.editable.CategoryEditableDetailActivity
import studio.hcmc.reminisce.ui.activity.category.editable.CategoryTitleEditDialog
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCategoryDetailBinding
    private lateinit var adapter: CategoryDetailAdapter
    private lateinit var locations: List<LocationVO>

    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val categoryTitle by lazy { intent.getStringExtra("categoryTitle") }

    private val users = HashMap<Int /* UserId */, UserVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()

    private val contents = ArrayList<CategoryDetailAdapter.Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.categoryDetailAppbar.apply {
            appbarTitle.text = getText(R.string.header_view_holder_title)
            appbarActionButton1.isVisible = false
            appbarBack.setOnClickListener { finish() }
        }

        viewBinding.categoryDetailAddButton.setOnClickListener {
            Intent(this@CategoryDetailActivity, WriteActivity::class.java).apply {
                putExtra("categoryId", categoryId)
                startActivity(this)
            }
        }

        loadContents()
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryDetailActivity)
        val fetch: suspend () -> List<LocationVO>
        if (categoryTitle == "Default") {
            fetch = { LocationIO.listByUserId(user.id) }
        } else {
            fetch = { LocationIO.listByCategoryId(categoryId) }
        }

        val result = runCatching { fetch() }
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
            CommonError.onMessageDialog(this@CategoryDetailActivity, "", "목록을 불러오는데 실패했어요. \n 다시 실행해 주세요.")
            LocalLogger.e("load Fail")
        }
    }

    private fun prepareContents() {
        contents.add(CategoryDetailAdapter.HeaderContent(categoryTitle!!))
        for ((date, locations) in locations.groupBy { it.createdAt.toString().substring(0, 7) }.entries) {
            val (year, month) = date.split("-")
            contents.add(CategoryDetailAdapter.DateContent(getString(R.string.card_date_separator, year, month.trim('0'))))
            for (location in locations.sortedByDescending { it.id }) {
                contents.add(CategoryDetailAdapter.DetailContent(
                    location,
                    tagInfo[location.id].orEmpty(),
                    friendInfo[location.id].orEmpty()
                ))
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.categoryDetailItems.layoutManager = LinearLayoutManager(this)
        adapter = CategoryDetailAdapter(
            adapterDelegate,
            headerDelegate,
            summaryDelegate
        )
        viewBinding.categoryDetailItems.adapter = adapter
    }

    private val adapterDelegate = object : CategoryDetailAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : CategoryDetailHeaderViewHolder.Delegate {
        override fun onItemClick() {
            Intent(this@CategoryDetailActivity, CategoryEditableDetailActivity::class.java).apply {
                putExtra("categoryTitle", categoryTitle)
                putExtra("categoryId", categoryId)
                startActivity(this)
            }
        }

        override fun onTitleEditClick() {
            CategoryTitleEditDialog(this@CategoryDetailActivity, dialogDelegate)
        }
    }

    private val dialogDelegate = object : CategoryTitleEditDialog.Delegate {
        override fun onSaveClick(editedTitle: String?) {
            onFetchTitle(editedTitle ?: categoryTitle!!)
        }
    }

    private fun onFetchTitle(body: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryDetailActivity)
        val dto = CategoryDTO.Patch().apply {
            title = body
        }
        runCatching { CategoryIO.patch(user.id, categoryId, dto ) }
            .onSuccess {
                withContext(Dispatchers.Main) {
                    contents[0] = CategoryDetailAdapter.HeaderContent(body)
                    adapter.notifyItemChanged(0)
                }
            }
            .onFailure { LocalLogger.e(it) }
    }

    private val summaryDelegate= object : CategoryDetailSummaryViewHolder.Delegate {
        override fun onItemClick(location: LocationVO) {
            Intent(this@CategoryDetailActivity, WriteDetailActivity::class.java).apply {
                putExtra("locationId", location.id)
                putExtra("title", location.title)
                startActivity(this)
            }
        }

        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }
    }
}