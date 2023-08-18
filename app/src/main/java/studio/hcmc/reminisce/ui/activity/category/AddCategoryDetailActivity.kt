package studio.hcmc.reminisce.ui.activity.category

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import studio.hcmc.reminisce.databinding.ActivityAddCategoryBinding
import studio.hcmc.reminisce.ui.activity.home.HomeActivity
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AddCategoryDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAddCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val appBar = viewBinding.addCategoryAppbar
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarTitle.text = "새로운 폴더"
        appBar.appbarBack.setOnClickListener { finish() }
        appBar.appbarActionButton1.setOnClickListener {
            if (viewBinding.addCategoryField.string.length <= 15) {
                Intent(this, HomeActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }

        val inputField = viewBinding.addCategoryField
        inputField.endIconMode = END_ICON_CLEAR_TEXT
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.text.length <= 15
        }
    }
}