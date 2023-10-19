package studio.hcmc.reminisce.ui.activity.home

import android.content.Intent
import android.os.Bundle
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
import studio.hcmc.reminisce.ui.view.Navigation
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location_friend.LocationFriendVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class HomeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityHomeBinding
    private lateinit var categories: List<CategoryVO>
    private lateinit var tags: List<TagVO>
    private lateinit var friendsInfo: List<FriendVO>
    private lateinit var friendTags: List<LocationFriendVO>
    private val cityTags = ArrayList<String>()

    private val users = HashMap<Int /* UserId */, UserVO>()
    private val categoryInfo = HashMap<Int /* categoryId */, Int /* countById */>()

    // TODO contents들 arrayList로 만들어 adapter에게 넘길 것
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
//        CoroutineScope(Dispatchers.IO).launch { fetchContents() }
    }

    private fun initView() {
        val menuId = intent.getIntExtra("selectedMenuId", -1)
        viewBinding.apply { homeNavView.navItems.selectedItemId = menuId }

        navController()
        CoroutineScope(Dispatchers.IO).launch { fetchContents() }
    }

    private suspend fun fetchContents() = coroutineScope {
        val result = runCatching {
            val user = UserExtension.getUser(this@HomeActivity)
            listOf(
                launch { categories = CategoryIO.listByUserId(user.id) },
                launch { tags = TagIO.listByUserId(user.id) },
                launch { friendsInfo = FriendIO.listByUserId(user.id) },
                launch { friendTags = LocationFriendIO.listByUserId(user.id) }
            ).joinAll()

            for (friend in friendsInfo) {
                if (friend.nickname == null) {
                    // IO 코드 호출하는 애가 try catch, runCatching 해줘야 함
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent
                }
            }

            for (category in categories) {
                if (category.title == "Default") {
                    val totalCount = CategoryIO.getTotalCountByUserId(user.id).get("totalCount")
                    categoryInfo[category.id] = totalCount.asInt
                } else {
                    val count = CategoryIO.getCountByCategoryIdAndUserId(user.id, category.id).get("count")
                    categoryInfo[category.id] = count.asInt
                }
            }
        }

        if (result.isSuccess) {
            withContext(Dispatchers.Main) { onContentsReady() }
        } else {
            // TODO handle error
            withContext(Dispatchers.Main) {
                CommonError.onDialog(this@HomeActivity)
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.homeItems.layoutManager = LinearLayoutManager(this)
        viewBinding.homeItems.adapter = HomeAdapter(
            headerDelegate,
            categoryDelegate,
            friendTagDelegate,
//            cityDelegate,
            tagDelegate
        )
    }

    private val headerDelegate = object : HeaderViewHolder.Delegate {
        override fun onClick() {
            Intent(this@HomeActivity, AddCategoryActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private val categoryDelegate = object : CategoryViewHolder.Delegate {
        override val categories: List<CategoryVO>
            get() = this@HomeActivity.categories
        override val categoryInfo: Map<Int, Int>
            get() = this@HomeActivity.categoryInfo

        override fun onCategoryClick(category: CategoryVO) {
            Intent(this@HomeActivity, CategoryDetailActivity::class.java).apply {
                putExtra("categoryId", category.id)
                putExtra("categoryTitle", category.title)
                startActivity(this)
            }
        }
    }

    private val tagDelegate = object : TagViewHolder.Delegate {
        override val tags get() = this@HomeActivity.tags
        override fun onTagClick(tag: TagVO) {
            Intent(this@HomeActivity, TagDetailActivity::class.java).apply {
                putExtra("tagId", tag.id)
                putExtra("tagTitle", tag.body)


//                putExtra("Tag", TagModal::class.java)
                startActivity(this)
            }
        }
    }

    private val friendTagDelegate = object : FriendTagViewHolder.Delegate {
        override val friends: List<FriendVO>
            get() = this@HomeActivity.friendsInfo

        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }

        override fun onTagClick(friendTag: FriendVO) {
            Intent(this@HomeActivity, FriendTagDetailActivity::class.java).apply {
                putExtra("opponentId", friendTag.opponentId)
                startActivity(this)
            }
        }
    }

//    private val cityDelegate = object : CityTagViewHolder.Delegate {
//        override val cityTags: List<String>
//            get() = this@HomeActivity.cityTags
//
//        override fun onTagClick(cityTag: String) {
//            Intent(this@HomeActivity, CategoryDetailActivity::class.java).apply {
//                putExtra("cityId", cityTag)
//                startActivity(this)
//            }
//        }
//    }

    private fun navController() {
        viewBinding.homeNavView.navItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_main_home -> { true }
                R.id.nav_main_map -> {
                    startActivity(Navigation.onNextMap(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_report -> {
                    startActivity(Navigation.onNextReport(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_setting -> {
                    startActivity(Navigation.onNextSetting(applicationContext, it.itemId))
                    finish()

                    true
                }
                else -> false
            }
        }
    }
}
