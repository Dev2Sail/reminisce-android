package studio.hcmc.reminisce.ui.activity.category

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityCategoryTitleEditBinding
import studio.hcmc.reminisce.dto.category.CategoryDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class CategoryTitleEditActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityCategoryTitleEditBinding
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val originalTitle by lazy { intent.getStringExtra("originalTitle") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCategoryTitleEditBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.apply {
            categoryTitleEditAppbar.appbarTitle.text = getText(R.string.category_title_edit_appbar)
            categoryTitleEditAppbar.appbarActionButton1.isEnabled = false
            categoryTitleEditField.editText!!.hint = originalTitle
            categoryTitleEditAppbar.appbarBack.setOnClickListener { finish() }
            categoryTitleEditAppbar.appbarActionButton1.setOnClickListener {
                if (categoryTitleEditField.string.length <= 15) {
                    fetchCategoryTitle(categoryTitleEditField.string)
                }
            }

            categoryTitleEditField.editText!!.addTextChangedListener {
                categoryTitleEditAppbar.appbarActionButton1.isEnabled = categoryTitleEditField.text.isNotEmpty() && categoryTitleEditField.string.length <= 15
            }
        }
    }

    private fun fetchCategoryTitle(editedTitle: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@CategoryTitleEditActivity)
        val patchDTO = CategoryDTO.Patch().apply {
            title = editedTitle
        }
        runCatching { CategoryIO.patch(user.id, categoryId, patchDTO) }
            .onSuccess {
                Intent(this@CategoryTitleEditActivity, CategoryDetailActivity::class.java).apply {
                    putExtra("fetchResult", true)
                    putExtra("fetchTitle", editedTitle)
                    finish()
                    startActivity(this)
                }
            }
            .onFailure {
//                Intent(this@CategoryTitleEditActivity, CategoryDetailActivity::class.java).apply {
//                    putExtra("fetchResult", false)
//                    startActivity(this)
//                    finish()
//                }
                CommonError.onDialog(this@CategoryTitleEditActivity)
                LocalLogger.e(it)
            }
    }
}
