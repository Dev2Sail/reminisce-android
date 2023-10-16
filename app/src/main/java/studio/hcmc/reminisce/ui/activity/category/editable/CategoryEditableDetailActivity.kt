package studio.hcmc.reminisce.ui.activity.category.editable

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityCategoryEditableDetailBinding
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.ui.activity.category.EditableCategoryDetail
import studio.hcmc.reminisce.ui.activity.category.SummaryModal
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.Logger
import studio.hcmc.reminisce.vo.location.LocationVO

class CategoryEditableDetailActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityCategoryEditableDetailBinding
    private lateinit var locations: List<LocationVO>
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val categoryTitle by lazy { intent.getStringExtra("categoryTitle") }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCategoryEditableDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.apply {
            categoryEditableDetailAppbar.appbarTitle.text = categoryTitle
            categoryEditableDetailAppbar.appbarBack.setOnClickListener { finish() }
            categoryEditableDetailAppbar.appbarActionButton1.text = getText(R.string.dialog_remove)
            categoryEditableDetailAppbar.appbarActionButton1.setOnClickListener {
                Toast.makeText(this@CategoryEditableDetailActivity, "click delete", Toast.LENGTH_SHORT).show()

            }

            // '삭제' 클릭 시 체크된 항목이 지워지고 다시 CategoryDetail로 Intent

        }
        prepareContents()


    }

    // buildContents
    private fun buildContents(): List<EditableCategoryDetail> {
        val contents = ArrayList<EditableCategoryDetail>()
        val sortedList = locations.sortedByDescending { it.createdAt }
        for ((summaryIdx, content) in sortedList.withIndex()) {
            contents.add(SummaryModal(
                content.id, content.title, content.visitedAt, content.latitude, content.longitude
            ))
        }

        return contents
    }


    private fun prepareContents() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.listByCategoryId(categoryId) }
            .onSuccess {
                locations = it
                withContext(Dispatchers.Main) { onContentsReady() }
            }
            .onFailure {
                CommonError.onDialog(this@CategoryEditableDetailActivity)
                Logger.v("reminisce Logger", "[reminisce > Category Detail > prepareContents] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
            }
    }

    private fun onContentsReady() {
        viewBinding.categoryEditableDetailItems.layoutManager = LinearLayoutManager(this)
        viewBinding.categoryEditableDetailItems.adapter = CategoryEditableDetailAdapter(buildContents())
    }


}


