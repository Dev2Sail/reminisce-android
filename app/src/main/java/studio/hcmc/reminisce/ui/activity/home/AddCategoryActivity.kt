package studio.hcmc.reminisce.ui.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityCategoryAddBinding
import studio.hcmc.reminisce.dto.category.CategoryDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.stringOrNull
import studio.hcmc.reminisce.util.text

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCategoryAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.apply {
            addCategoryAppbar.appbarTitle.text = getText(R.string.add_category_title)
            addCategoryAppbar.appbarBack.setOnClickListener { finish() }
            addCategoryAppbar.appbarActionButton1.setOnClickListener {
                if (addCategoryField.stringOrNull.isNullOrEmpty()) {
                    addCategory("new")
                } else if (addCategoryField.string.length <= 15) {
                    addCategory(addCategoryField.string)
                }

                addCategoryField.text.clear()
            }
        }
    }

    private fun addCategory(body: String?) = CoroutineScope(Dispatchers.IO).launch {
        val postDTO = CategoryDTO.Post().apply {
            userId = UserExtension.getUser(this@AddCategoryActivity).id
            title = body
        }

        runCatching { CategoryIO.post(postDTO) }
            .onSuccess {
                Intent(this@AddCategoryActivity, HomeActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }.onFailure {
                CommonError.onMessageDialog(this@AddCategoryActivity, "폴더 생성 실패", "폴더를 추가하는데 실패했어요. \n다시 시도해 주세요.")
                LocalLogger.e(it)
            }
    }
}