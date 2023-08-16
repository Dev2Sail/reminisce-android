package studio.hcmc.reminisce.ui.activity.category

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.databinding.ActivityCategoryDetailBinding
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.vo.location.LocationVO

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCategoryDetailBinding

    private val summaryList = ArrayList<LocationVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

//        viewBinding.categoryDetailAppbar.appbarTitle.text = intent.getStringExtra("categoryTitle")
        viewBinding.categoryDetailAppbar.appbarTitle.text = "홈"
        viewBinding.categoryDetailAppbar.appbarActionButton1.isVisible = false
        viewBinding.categoryDetailItems.adapter = CategoryDetailAdapter(summaryDelegate, categoryHeaderDelegate)

        viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener {
            finish()
        }

        viewBinding.categoryDetailAddButton.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
            intent.putExtra("selectedCategory", "folder name")
            startActivity(intent)
        }
    }

    private val summaryDelegate= object : SummaryViewHolder.Delegate {
        override val summaryList: List<LocationVO>
            get() = this@CategoryDetailActivity.summaryList

        override fun onSummaryClick(summary: LocationVO) {
            Toast.makeText(this@CategoryDetailActivity, "clicked summary", Toast.LENGTH_SHORT).show()
        }
    }

    private val categoryHeaderDelegate = object : CategoryDetailHeaderViewHolder.Delegate {
        override fun onClick() {
            Intent(this@CategoryDetailActivity, CategoryDetailEditableHeaderViewHolder::class.java).apply {
                startActivity(this)
            }
        }
    }
}
