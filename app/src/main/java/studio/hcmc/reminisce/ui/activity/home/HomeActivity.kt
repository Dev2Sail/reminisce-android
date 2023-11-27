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
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityHomeBinding
import studio.hcmc.reminisce.dto.category.CategoryDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.category.CategoryDetailActivity
import studio.hcmc.reminisce.ui.activity.friend_tag.FriendTagDetailActivity
import studio.hcmc.reminisce.ui.activity.map.MapTestActivity
import studio.hcmc.reminisce.ui.activity.tag.TagDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.tag.TagVO

class HomeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityHomeBinding
    private lateinit var adapter: HomeAdapter

    private var defaultCategoryId = -1
    private val categories = ArrayList<CategoryVO>()
    private val countByCategoryId = HashMap<Int /* categoryId */, Int /* countById */>()
    private val tags = ArrayList<TagVO>()
    private val friends = ArrayList<FriendVO>()
    private val contents = ArrayList<HomeAdapter.Content>()

    private val categoryDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onCategoryEditResult)
    private val friendDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onFriendModifiedResult)
    private val tagDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onTagModifiedResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun test() {
        viewBinding.homeTest.setOnClickListener {
            Intent(this, MapTestActivity::class.java).apply {
                startActivity(this)

            }
        }
    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.homeNavView, menuId)
        CoroutineScope(Dispatchers.IO).launch { patchContents() }
        test()
    }

    private suspend fun patchContents() = coroutineScope {
        val result = runCatching {
            val user = UserExtension.getUser(this@HomeActivity)
            listOf(
                launch { categories.addAll(CategoryIO.listByUserId(user.id).sortedBy { it.sortOrder }) },
                launch { tags.addAll(TagIO.listByUserId(user.id).sortedByDescending { it.id }) },
                launch { friends.addAll(FriendIO.distinctListByUserId(user.id).sortedBy { it.nickname }) }
//                launch { LocationFriendIO.listByUserId(user.id).mapTo(friendTagOpponentIds) { it.opponentId } }
            ).joinAll()

//            friends = friendTagOpponentIds.mapTo(ArrayList(friendTagOpponentIds.size)) { FriendIO.getByUserIdAndOpponentId(user.id, it) }
//            friends.sortByDescending { it.requestedAt }

            for (category in categories) {
                when (category.title) {
                    "Default" -> {
                        defaultCategoryId = category.id
                        val totalCount = CategoryIO.getTotalCountByUserId(user.id).get("totalCount").asInt
                        countByCategoryId[category.id] = totalCount
                    }
                    else -> {
                        val count = CategoryIO.getCountByCategoryIdAndUserId(user.id, category.id).get("count").asInt
                        countByCategoryId[category.id] = count
                    }
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
        contents.add(HomeAdapter.HeaderContent())
        contents.addAll(categories.map { HomeAdapter.CategoryContent(it, countByCategoryId[it.id] ?: 0) })
        contents.add(HomeAdapter.TagContent(tags))
        contents.add(HomeAdapter.FriendContent(friends))
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

    /* Add Category */
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
            .onSuccess { it ->
                val count = CategoryIO.getCountByCategoryIdAndUserId(user.id, it.id).get("count").asInt
                categories.add(it)
                countByCategoryId[it.id] = count
                categories.sortedBy { it.sortOrder }
                contents.add(categories.size, HomeAdapter.CategoryContent(it, count))
                withContext(Dispatchers.Main) { adapter.notifyItemInserted(categories.size) }
            }.onFailure {
                LocalLogger.e(it)
                withContext(Dispatchers.Main) { onError() }
            }
    }

    private fun onError() {
        CommonError.onMessageDialog(this@HomeActivity, getString(R.string.dialog_error_add_folder))
    }

    private val categoryDelegate = object : CategoryViewHolder.Delegate {
        override fun onItemClick(categoryId: Int, position: Int) {
            val intent = Intent(this@HomeActivity, CategoryDetailActivity::class.java)
                .putExtra("categoryId", categoryId)
                .putExtra("position", position)
            categoryDetailLauncher.launch(intent)
        }

        override fun onItemLongClick(categoryId: Int, position: Int) {
            DeleteCategoryDialog(this@HomeActivity, categoryId, position, deleteDialogDelegate)
        }
    }

    /* Delete Category */
    private val deleteDialogDelegate = object : DeleteCategoryDialog.Delegate {
        override fun onDeleteClick(categoryId: Int, position: Int) {
            onDeleteContent(categoryId, position)
        }
    }

    private fun onDeleteContent(categoryId: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch{
        runCatching { CategoryIO.delete(categoryId) }
            .onSuccess {
                categories.removeAt(position)
                countByCategoryId.remove(categoryId)
                contents.removeAt(position)
                withContext(Dispatchers.Main) { adapter.notifyItemRemoved(position + 1) }
            }.onFailure { LocalLogger.e(it) }
    }

    /* TAG */
    private val tagDelegate = object : TagViewHolder.Delegate {
        override fun onItemClick(tagId: Int) {
            val intent = Intent(this@HomeActivity, TagDetailActivity::class.java)
                .putExtra("tagId", tagId)
            tagDetailLauncher.launch(intent)
        }

        override fun onItemLongClick(tagId: Int, tagIdx: Int, position: Int) {
            DeleteTagDialog(this@HomeActivity, tagId, tagIdx, position, deleteTagDelegate)
        }
    }

    // tagDelegate.onItemLongClick() -> deleteTagDelegate -> onDeleteTag
    private val deleteTagDelegate = object : DeleteTagDialog.Delegate {
        override fun onDeleteClick(tagId: Int, tagIdx: Int, position: Int) {
            onDeleteTag(tagId, tagIdx, position)
        }
    }

    private fun onDeleteTag(tagId: Int, tagIdx: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { TagIO.delete(tagId) }
            .onSuccess {
                tags.removeAt(tagIdx)
                contents[position] = HomeAdapter.TagContent(tags)
                withContext(Dispatchers.Main) { adapter.notifyItemChanged(position) }
            }.onFailure { LocalLogger.e(it) }
    }

    private val friendTagDelegate = object : FriendTagViewHolder.Delegate {
        override fun onItemClick(opponentId: Int, nickname: String) {
            val intent = Intent(this@HomeActivity, FriendTagDetailActivity::class.java)
                .putExtra("opponentId", opponentId)
                .putExtra("nickname", nickname)
            friendDetailLauncher.launch(intent)
        }

        override fun onItemLongClick(opponentId: Int, friendIdx: Int, position: Int) {
            DeleteFriendTagDialog(this@HomeActivity, opponentId, friendIdx, position, deleteFriendDelegate)
        }
    }

    private val deleteFriendDelegate = object :DeleteFriendTagDialog.Delegate {
        override fun onDeleteClick(opponentId: Int, friendIdx: Int, position: Int) {
            onDeleteFriend(opponentId, friendIdx, position)
        }
    }

    private fun onDeleteFriend(opponentId: Int, friendIdx: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@HomeActivity)
        runCatching { FriendIO.delete(user.id, opponentId) }
            .onSuccess {
                friends.removeAt(friendIdx)
                contents[position] = HomeAdapter.FriendContent(friends)
                withContext(Dispatchers.Main) { adapter.notifyItemChanged(position) }
            }.onFailure { LocalLogger.e(it) }
    }



    private fun onCategoryEditResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isEdited", false) == true) {
            val categoryId = activityResult.data?.getIntExtra("categoryId", -1)
            val position = activityResult.data?.getIntExtra("position", -1)
            onPatchCategory(categoryId!!, position!!)
            onPatchFriend()
        }
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            val categoryId = activityResult.data?.getIntExtra("categoryId", -1)
            val position = activityResult.data?.getIntExtra("position", -1)
            onPatchCategory(categoryId!!, position!!)
            onPatchTotalCount()
            onPatchFriend()
        }
    }

    private fun onFriendModifiedResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            onPatchFriend()
        }
    }

    private fun onTagModifiedResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            onPatchFriend()
        }
    }

    // TODO 1122 추가 테스트 필요
    private fun onPatchFriend() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@HomeActivity)
        val result = runCatching { FriendIO.distinctListByUserId(user.id) }
            .onSuccess {
                friends.clear()
                for (vo in it) {
                    friends.add(vo)
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            contents.removeAt(contents.size - 1)
            contents.add(HomeAdapter.FriendContent(friends))
            withContext(Dispatchers.Main) {
                adapter.notifyItemChanged(contents.size - 1)
            }
        }
    }

    private fun onPatchCategory(categoryId: Int, position: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@HomeActivity)
        runCatching { CategoryIO.getById(categoryId) }
            .onSuccess {
                val count = CategoryIO.getCountByCategoryIdAndUserId(user.id, categoryId).get("count").asInt
                contents[position] = HomeAdapter.CategoryContent(it, count)
                withContext(Dispatchers.Main) { adapter.notifyItemChanged(position) }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun onPatchTotalCount() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@HomeActivity)
        runCatching { CategoryIO.getTotalCountByUserId(user.id) }
            .onSuccess {
                val count = it.get("totalCount").asInt
                val first = categories[0]
                countByCategoryId[first.id] = count
                contents[1] = HomeAdapter.CategoryContent(first, count)
                withContext(Dispatchers.Main) { adapter.notifyItemChanged(1) }
            }.onFailure { LocalLogger.e(it) }
    }
}
