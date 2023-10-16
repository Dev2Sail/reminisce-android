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
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.ui.activity.category.editable.CategoryEditableDetailActivity
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.Logger
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

        initView()

//        CoroutineScope(Dispatchers.IO).launch { fetchContents() }
    }

    private fun initView() {
        // TODO location이 생성되지 않았을 때 view



        viewBinding.apply {
            categoryDetailAppbar.appbarTitle.text = getText(R.string.header_view_holder_title)
            categoryDetailAppbar.appbarActionButton1.isVisible = false
            categoryDetailAppbar.appbarBack.setOnClickListener { finish() }
            categoryDetailAddButton.setOnClickListener {
                Intent(this@CategoryDetailActivity, WriteActivity::class.java).apply {
                    putExtra("categoryId", categoryId)
                    startActivity(this)
                }
            }
        }

        // Category Title 변경 시 recyclerView에게 notify
//        if (intent.getBooleanExtra("titleFetchResult", false)) {
//            CategoryDetailAdapter(summaryDelegate, categoryHeaderDelegate).notifyItemChanged(0)
//        }

        prepareContents()
    }

    private fun prepareContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryDetailActivity)
        if (categoryTitle == "Default") {
            runCatching { LocationIO.listByUserId(user.id) }
                .onSuccess {
                    locations = it
                    withContext(Dispatchers.Main) { onContentsReady() }
                }
                .onFailure {
                    CommonError.onMessageDialog(this@CategoryDetailActivity, "불러오기 오류", "추억을 불러오는데 실패했어요. \n 어플을 재실행해 주세요.")
                    Logger.v("reminisce Logger", "[reminisce > Category Detail > prepareContents(Default)] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
                }
        } else {
            runCatching { LocationIO.listByCategoryId(categoryId) }
                .onSuccess {
                    locations = it
                    withContext(Dispatchers.Main) { onContentsReady() }
                }
                .onFailure {
                    CommonError.onMessageDialog(this@CategoryDetailActivity, "불러오기 오류", "추억을 불러오는데 실패했어요. \n 어플을 재실행해 주세요.")
                    Logger.v("reminisce Logger", "[reminisce > Category Detail > prepareContents] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
                }
        }
    }

    private fun buildContents() {
//        val categoryContents = ArrayList<>()
    }

    private fun onContentsReady() {
        viewBinding.categoryDetailItems.layoutManager = LinearLayoutManager(this)
        viewBinding.categoryDetailItems.adapter = CategoryDetailAdapter(summaryDelegate, categoryHeaderDelegate)
    }

    private val categoryHeaderDelegate = object : CategoryDetailHeaderViewHolder.Delegate {
        override val title: String
            get() = this@CategoryDetailActivity.categoryTitle!!

        override fun onClick() {
            Intent(this@CategoryDetailActivity, CategoryEditableDetailActivity::class.java).apply {
                putExtra("categoryTitle", categoryTitle)
                putExtra("categoryId", categoryId)
                startActivity(this)
            }
        }

        override fun onTitleClick() {
            Intent(this@CategoryDetailActivity, CategoryTitleEditActivity::class.java).apply {
                putExtra("originalCategoryTitle", categoryTitle)
                putExtra("categoryId", categoryId)
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