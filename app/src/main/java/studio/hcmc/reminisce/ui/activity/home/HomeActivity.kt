package studio.hcmc.reminisce.ui.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import studio.hcmc.reminisce.databinding.ActivityHomeBinding
import studio.hcmc.reminisce.ui.activity.category.AddCategoryDetailActivity
import studio.hcmc.reminisce.ui.activity.category.CategoryDetailActivity
import studio.hcmc.reminisce.ui.activity.setting.SettingActivity
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.location_friend.LocationFriendVO
import studio.hcmc.reminisce.vo.tag.TagVO
import java.sql.Timestamp

class HomeActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityHomeBinding

    private val tags = ArrayList<TagVO>().apply {
        add(TagVO(3, 4, "hello", Timestamp(3)))
    }
    private val categories = ArrayList<CategoryVO>().apply {
        add(CategoryVO(1, 30, 2, "category 2", false, Timestamp(3)))
        add(CategoryVO(2, 30, 3, "category 3", false, Timestamp(3)))
        add(CategoryVO(3, 30, 4, "category 4", false, Timestamp(3)))
        add(CategoryVO(4, 30, 5, "category 5", false, Timestamp(3)))
        add(CategoryVO(5, 30, 6, "category 6", false, Timestamp(3)))
        add(CategoryVO(6, 30, 7, "category 7", false, Timestamp(3)))

    }
    private val friendTagList = ArrayList<LocationFriendVO>().apply {
        add(LocationFriendVO(1, 3))
    }

    private val cityTagList = ArrayList<String>().apply {
        add("인천광역시")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.homeItems.layoutManager = LinearLayoutManager(this)
        viewBinding.homeItems.adapter = HomeAdapter(
            headerDelegate, categoryDelegate, personTagDelegate, cityDelegate, tagDelegate
        )



            /*---------------------------------------------------- */
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

    private val headerDelegate = object : HeaderViewHolder.Delegate {
        override fun onClick() {
            Intent(this@HomeActivity, AddCategoryDetailActivity::class.java).apply {
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
//                putExtra("categories", categories)
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
        override val friendTags: List<LocationFriendVO>
            get() = this@HomeActivity.friendTagList

        override fun onTagClick(friendTag: LocationFriendVO) {
            Intent(this@HomeActivity, CategoryDetailActivity::class.java).apply {
                putExtra("locationId", friendTag.locationId)
                startActivity(this)
            }
        }
    }

    private val cityDelegate = object : CityTagViewHolder.Delegate {
        override val cityTags: List<String>
            get() = this@HomeActivity.cityTagList

        override fun onTagClick(cityTag: String) {
            Intent(this@HomeActivity, CategoryDetailActivity::class.java).apply {
                putExtra("cityId", cityTag)
                startActivity(this)
            }
        }
    }
}