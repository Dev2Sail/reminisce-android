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
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.category.CategoryDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.ui.view.Navigation
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class HomeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityHomeBinding
    private lateinit var categories: List<CategoryVO>
    private lateinit var tags: List<TagVO>
    private lateinit var friends: List<FriendVO>
    private val cityTags = ArrayList<String>()

    private val users = HashMap<Int /* UserId */, UserVO>()
//    private val categoryInfo = HashMap<Int /* categoryId */, Int /* count */>()


    // TODO contents들 arrayList로 만들어 adapter에게 넘길 것
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val menuId = intent.getIntExtra("selectedMenuId", -1)
        viewBinding.homeNavView.navItems.selectedItemId = menuId
        navController()
        CoroutineScope(Dispatchers.IO).launch { fetchContents() }
    }

    private suspend fun fetchContents() = coroutineScope {
        val result = runCatching {
            val user = UserExtension.getUser(this@HomeActivity)
            listOf(
                launch { categories = CategoryIO.listByUserId(user.id) },
                launch { tags = TagIO.listByUserId(user.id) },
                launch { friends = FriendIO.listByUserId(user.id) }
            ).joinAll()

            for (friend in friends) {
                if (friend.nickname == null) {
                    val opponent = UserIO.getById(friend.opponentId)
                    users[opponent.id] = opponent
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
        viewBinding.homeItems.adapter = HomeAdapter(headerDelegate, categoryDelegate, personTagDelegate, cityDelegate, tagDelegate)
    }

    private fun navController() {
        viewBinding.homeNavView.navItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_main_home -> {
                    true
                }

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

    private val headerDelegate = object : HeaderViewHolder.Delegate {
        override fun onClick() {
            Intent(this@HomeActivity, AddCategoryActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(this)
            }
        }
    }

    private val categoryDelegate = object : CategoryViewHolder.Delegate {
        override val categories: List<CategoryVO>
            get() = this@HomeActivity.categories

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
            Intent(this@HomeActivity, CategoryDetailActivity::class.java).apply {
                putExtra("tagId", tag.id)
                startActivity(this)
            }
        }
    }

    private val personTagDelegate = object : FriendTagViewHolder.Delegate {
        override val friends: List<FriendVO>
            get() = this@HomeActivity.friends

        override fun getUser(userId: Int): UserVO {
            return users[userId]!!
        }

        override fun onTagClick(friendTag: FriendVO) {
//            Intent(this@HomeActivity, CategoryDetailActivity::class.java).apply {
//                putExtra("locationId", friendTag.locationId)
//                startActivity(this)
//            }
        }
    }

    private val cityDelegate = object : CityTagViewHolder.Delegate {
        override val cityTags: List<String>
            get() = this@HomeActivity.cityTags

        override fun onTagClick(cityTag: String) {
            Intent(this@HomeActivity, CategoryDetailActivity::class.java).apply {
                putExtra("cityId", cityTag)
                startActivity(this)
            }
        }
    }
}
