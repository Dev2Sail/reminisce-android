package studio.hcmc.reminisce.ui.activity.category

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityCategoryDetailBinding
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.vo.location.LocationVO

class CategoryDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCategoryDetailBinding
    private lateinit var locations: List<LocationVO>
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val categoryTitle by lazy { intent.getStringExtra("categoryTitle") }
    private val summaryList = ArrayList<LocationVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.categoryDetailAppbar.appbarTitle.text = getText(R.string.header_view_holder_title)
        viewBinding.categoryDetailAppbar.appbarActionButton1.isVisible = false
        viewBinding.categoryDetailAppbar.appbarBack.setOnClickListener { finish() }

        viewBinding.categoryDetailAddButton.setOnClickListener {
            Intent(this, WriteActivity::class.java).apply {
                putExtra("categoryId", categoryId)
                startActivity(this)
            }
        }

        prepareContents()

//        CoroutineScope(Dispatchers.IO).launch { fetchContents() }
    }

  private fun prepareContents() = CoroutineScope(Dispatchers.IO).launch {
      runCatching { LocationIO.listByCategoryId(categoryId) }
          .onSuccess {
              locations = it
              withContext(Dispatchers.Main) { onContentsReady() }
          }
          .onFailure {

              CommonError.onDialog(this@CategoryDetailActivity)
          }
  }

    private fun onContentsReady() {
        viewBinding.categoryDetailItems.layoutManager = LinearLayoutManager(this)
        viewBinding.categoryDetailItems.adapter = CategoryDetailAdapter(summaryDelegate, categoryHeaderDelegate)
    }

    private val categoryHeaderDelegate = object : CategoryDetailHeaderViewHolder.Delegate {
        override val title: String
            get() = this@CategoryDetailActivity.categoryTitle!!

        override fun onClick() {
            Intent(this@CategoryDetailActivity, CategoryDetailEditableHeaderViewHolder::class.java).apply {
                startActivity(this)
            }
        }
    }

    private val summaryDelegate= object : SummaryViewHolder.Delegate {
        override val locations: List<LocationVO>
            get() = this@CategoryDetailActivity.locations

        override fun onSummaryClick(location: LocationVO) {
            Toast.makeText(this@CategoryDetailActivity, "clicked summary", Toast.LENGTH_SHORT).show()
        }
    }
}

// TODO 카테고리 icon 클릭 시 category id에 해당하는 location 정보 표시
// TODO 카테고리 date separator 월별 구분 -> activity에서 contents 만들어서 넘겨야 함 !
// TODO '한눈에 보기' 클릭 시 uesr의 모든 location 정보 표시
/*
categoryDetail에서 '편집' 눌렀을 때
새로운 헤더와 clickable summary가 담긴 리사이클러뷰...

location에 visitedAt: String으로 데이터베이스 추가 && dto 수정, vo 수정, repository, service 수정

 */