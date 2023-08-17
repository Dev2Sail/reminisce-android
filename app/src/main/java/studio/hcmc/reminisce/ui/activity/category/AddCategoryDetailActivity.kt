package studio.hcmc.reminisce.ui.activity.category

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import studio.hcmc.reminisce.databinding.ActivityAddCategoryBinding
import studio.hcmc.reminisce.ui.activity.home.HomeActivity
import studio.hcmc.reminisce.util.string

class AddCategoryDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAddCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.addCategoryAppbar.appbarTitle.text = "새로운 폴더"
        viewBinding.addCategoryAppbar.appbarBack.setOnClickListener {
            finish()
        }
        viewBinding.addCategoryField.endIconMode = END_ICON_CLEAR_TEXT
        viewBinding.addCategoryAppbar.appbarActionButton1.setOnClickListener {
            if (viewBinding.addCategoryField.string.length <= 15) {
                Intent(this, HomeActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }
}