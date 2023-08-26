package studio.hcmc.reminisce.ui.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityCategoryAddBinding
import studio.hcmc.reminisce.dto.category.CategoryDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCategoryAddBinding
    // TODO CREATE ERROR DIALOG
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val appBar = viewBinding.addCategoryAppbar
        val inputField = viewBinding.addCategoryField
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarTitle.text = getText(R.string.add_category_title)
        appBar.appbarBack.setOnClickListener { finish() }
        appBar.appbarActionButton1.setOnClickListener {
            if (inputField.string.length <= 15) {
                addCategory()
            }
            inputField.text.clear()
        }

        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.text.length <= 15
        }
    }

    private fun addCategory() = CoroutineScope(Dispatchers.IO).launch {
        val postDTO = CategoryDTO.Post().apply {
            userId = UserExtension.getUser(this@AddCategoryActivity).id
            title = viewBinding.addCategoryField.string
        }

        runCatching { CategoryIO.post(postDTO) }
            .onSuccess {
                Intent(this@AddCategoryActivity, HomeActivity::class.java).apply {
                    startActivity(this)
                }
            }.onFailure {
                it.message
                it.cause
                it.stackTrace
            }
    }
}