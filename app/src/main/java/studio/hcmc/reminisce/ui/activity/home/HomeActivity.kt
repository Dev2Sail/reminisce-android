package studio.hcmc.reminisce.ui.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.databinding.ActivityHomeBinding
import studio.hcmc.reminisce.dto.category.CategoryDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationFriendIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.category.CategoryDetailActivity
import studio.hcmc.reminisce.ui.activity.friend_tag.FriendTagDetailActivity
import studio.hcmc.reminisce.ui.activity.tag.TagDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location_friend.LocationFriendVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class HomeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityHomeBinding
    private lateinit var adapter: HomeAdapter

    private lateinit var friends: List<FriendVO>
    private lateinit var friendTags: List<LocationFriendVO>
    private lateinit var tags: List<TagVO>

    private val users = HashMap<Int /* UserId */, UserVO>()
    private val categories = ArrayList<CategoryVO>()
    private val categoryInfo = HashMap<Int /* categoryId */, Int /* countById */>()
    private val contents = ArrayList<HomeAdapter.Content>()

    private val categoryTitleEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onCategoryEditResult)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.homeNavView, menuId)

        CoroutineScope(Dispatchers.IO).launch { fetchContents() }
    }

    private suspend fun fetchContents() = coroutineScope {
        val result = runCatching {
            val user = UserExtension.getUser(this@HomeActivity)
            listOf(
//                launch { categories = CategoryIO.listByUserId(user.id) },
                launch { categories.addAll(CategoryIO.listByUserId(user.id).sortedBy { it.sortOrder }) },
                launch { tags = TagIO.listByUserId(user.id) },
                launch { friends = FriendIO.listByUserId(user.id) },
                launch { friendTags = LocationFriendIO.listByUserId(user.id) }
            ).joinAll()

            for (friend in friends) {
                if (friend.nickname == null) {
                    // IO 코드 호출하는 애가 try catch, runCatching 해줘야 함
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent
                }
            }

            for (category in categories) {
                if (category.title == "Default") {
                    val totalCount = CategoryIO.getTotalCountByUserId(user.id).get("totalCount").asInt
                    categoryInfo[category.id] = totalCount
                } else {
                    val count = CategoryIO.getCountByCategoryIdAndUserId(user.id, category.id).get("count").asInt
                    categoryInfo[category.id] = count
                }
            }
        }.onFailure { LocalLogger.e(it) }

        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        } else {
            withContext(Dispatchers.Main) { CommonError.onDialog(this@HomeActivity) }
        }
    }

    private fun prepareContents() {
        val friendContent = ArrayList<FriendVO>()

        for (friend in friends) {
            for (tag in friendTags) {
                if (friend.opponentId == tag.opponentId) {
                    friendContent.add(friend)
                }
            }
        }

        contents.add(HomeAdapter.HeaderContent())
        categories.forEach { contents.add(HomeAdapter.CategoryContent(it, categoryInfo[it.id] ?: 0)) }
//        for (category in categories.sortedBy { it.sortOrder }) {
//            contents.add(HomeAdapter.CategoryContent(category, categoryInfo[category.id] ?: 0 ))
//        }
        contents.add(HomeAdapter.TagContent(tags))
        contents.add(HomeAdapter.FriendContent(friendContent.distinct()))
    }

    private fun onContentsReady() {
        viewBinding.homeItems.layoutManager = LinearLayoutManager(this)
        adapter = HomeAdapter(
            adapterDelegate,
            headerDelegate,
            categoryDelegate,
            tagDelegate,
            friendTagDelegate
        )
        viewBinding.homeItems.adapter = adapter
    }

    private val adapterDelegate = object : HomeAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : HeaderViewHolder.Delegate {
        override fun onClick() {
            AddCategoryDialog(this@HomeActivity, addDialogDelegate)
        }
    }

    private val addDialogDelegate = object : AddCategoryDialog.Delegate {
        override fun onSaveClick(body: String?) {
            onAddContent(body ?: "new")
        }
    }

    private fun onAddContent(body: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@HomeActivity)
        val dto = CategoryDTO.Post().apply {
            userId = user.id
            title = body
        }
        runCatching { CategoryIO.post(dto) }
            .onSuccess {
                val count = CategoryIO.getCountByCategoryIdAndUserId(user.id, it.id).get("count").asInt
                categories.add(it)
                categoryInfo[it.id] = count
                // TODO itemInserted
                // add 잘 되는데 한눈에 보기 (category[1]번째에 추가됨
                withContext(Dispatchers.Main) {
                    contents.add(HomeAdapter.CategoryContent(it, count))
//                    adapter.notifyItemInserted(1) // 한눈에 보기가 하나 더 생겨버림
//                    adapter.notifyItemChanged(1) // 맨 아래 생김
                    adapter.notifyItemRangeChanged(1, 1) // 맨 아래 생김
//                    adapter.notifyItemInserted(1)
//                    adapter.notifyItemRangeInserted(1, )
//                    adapter.notifyItemRangeChanged(1,1)
                }
            }.onFailure {
                LocalLogger.e(it)
                withContext(Dispatchers.Main) {
                    CommonError.onMessageDialog(this@HomeActivity, "폴더를 추가하는 데 실패했어요. \n다시 시도해 주세요.")
                }
            }
    }

    private val categoryDelegate = object : CategoryViewHolder.Delegate {
        override fun onItemClick(category: CategoryVO, position: Int) {
            Intent(this@HomeActivity, CategoryDetailActivity::class.java).apply {
                putExtra("categoryId", category.id)
                putExtra("position", position)
                startActivity(this)
            }
        }

        // to Delete Category Dialog
        override fun onItemLongClick(categoryId: Int, position: Int) {
            DeleteCategoryDialog(this@HomeActivity, categoryId, position, deleteDialogDelegate)
        }
    }

    // Delete Category Dialog delegate
    private val deleteDialogDelegate = object : DeleteCategoryDialog.Delegate {
        override fun onDeleteClick(categoryId: Int, position: Int) {
            LocalLogger.v("position: $position, contents size: ${contents.size}")

//            onDeleteContent(categoryId, position)
        }
    }

    private fun onDeleteContent(categoryId: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch{
        runCatching { CategoryIO.delete(categoryId) }
            .onSuccess {
                withContext(Dispatchers.Main) {
                        contents.removeAt(position)
                    categories.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            }.onFailure { LocalLogger.e(it) }
    }

    // TODO onItemLongClick() -> delete
    // TODO tag result (after delete)
    private val tagDelegate = object : TagViewHolder.Delegate {
        override fun onItemClick(tag: TagVO) {
            // TODO tagId만 보내서 거기서 요청
            Intent(this@HomeActivity, TagDetailActivity::class.java).apply {
                putExtra("tagId", tag.id)
                startActivity(this)
            }
        }

        override fun onItemClick2(tagId: Int) {
            // TODO only tag Id intent
        }

        override fun onItemLongClick(tagId: Int, position: Int) {
            // TODO delete tag
        }
    }

    // TODO onItemLongClick() -> delete
    private val friendTagDelegate = object : FriendTagViewHolder.Delegate {
        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }

        override fun onItemClick(friend: FriendVO) {
            Intent(this@HomeActivity, FriendTagDetailActivity::class.java).apply {
                putExtra("opponentId", friend.opponentId)
                putExtra("nickname", friend.nickname ?: users[friend.opponentId]!!.nickname)
                startActivity(this)
            }
        }
    }

    private fun onCategoryEditResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isEdited", false) == true) {
            val title = activityResult.data?.getStringExtra("editedTitle")
            val categoryId = activityResult.data?.getIntExtra("categoryId", -1)
            val position = activityResult.data?.getIntExtra("position", -1)
            onFetchCategory(categoryId!!, position!!)


        }
    }

    private fun onFetchCategory(categoryId: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@HomeActivity)
        runCatching { CategoryIO.getById(categoryId) }
            .onSuccess {
                val count = CategoryIO.getCountByCategoryIdAndUserId(user.id, categoryId).get("count").asInt
                withContext(Dispatchers.Main) {
                    contents[position] = HomeAdapter.CategoryContent(it, count)
                    adapter.notifyItemChanged(position)
                }
            }.onFailure { LocalLogger.e(it) }
    }
}
