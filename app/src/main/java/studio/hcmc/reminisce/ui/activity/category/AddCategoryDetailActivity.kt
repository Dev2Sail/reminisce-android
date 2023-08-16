package studio.hcmc.reminisce.ui.activity.category

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import studio.hcmc.reminisce.databinding.ActivityAddCategoryDetailBinding

class AddCategoryDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAddCategoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddCategoryDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.addCategoryDetailAppbar.appbarTitle.text = "홈"
        viewBinding.addCategoryDetailAppbar.appbarActionButton1.isVisible = false
        viewBinding.addCategoryDetailAppbar.appbarBack.setOnClickListener {
            finish()
        }

        val inputContainer = viewBinding.addCategoryDetail.commonEditableHeaderInput
        val saveButton = viewBinding.addCategoryDetail.commonEditableHeaderAction1
        inputContainer.placeholderText = "새로운 폴더"
        inputContainer.isCounterEnabled = true
        inputContainer.counterMaxLength = 15
        inputContainer.endIconMode = END_ICON_CLEAR_TEXT
        saveButton.text = "저장"
        saveButton.setOnClickListener {
            finish()
        }
    }
}