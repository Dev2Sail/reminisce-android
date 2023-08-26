package studio.hcmc.reminisce.ui.activity.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.databinding.ActivityHomeBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.category.CategoryDetailActivity
import studio.hcmc.reminisce.ui.activity.setting.SettingActivity
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.tag.TagVO
import studio.hcmc.reminisce.vo.user.UserVO

class HomeActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityHomeBinding
    private lateinit var categories: List<CategoryVO>
    private lateinit var tags: List<TagVO>
    private lateinit var friends: List<FriendVO>
    private val cityTags = ArrayList<String>()

    private val users = HashMap<Int /* UserId */, UserVO>()
    private val categoryInfo = HashMap<Int /* categoryId */, Int /* count */>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        test()

        CoroutineScope(Dispatchers.IO).launch { fetchContents() }
    }

    private fun test() {
        viewBinding.homeWriter.setOnClickListener {
            Intent(this, WriteActivity::class.java).apply {
                startActivity(this)
            }
        }
        viewBinding.homeSetting.setOnClickListener {
            Intent(this, SettingActivity::class.java).apply {
                startActivity(this)
            }
        }
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

//            for (category in categories) {
//                val count = CategoryIO.getCountByCategoryIdAndUserId(user.id, category.id).toString().toInt()
//                categoryInfo[category.id] = count
//            }
        }

        if (result.isSuccess) {
            withContext(Dispatchers.Main) { onContentsReady() }
        } else {
            // TODO handle error
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, "어플을 다시 실행해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.homeItems.layoutManager = LinearLayoutManager(this)
        viewBinding.homeItems.adapter = HomeAdapter(headerDelegate, categoryDelegate, personTagDelegate, cityDelegate, tagDelegate)
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

//        override fun getCount(categoryId: Int): Int {
//            return categoryInfo[categoryId]!!
//        }

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
