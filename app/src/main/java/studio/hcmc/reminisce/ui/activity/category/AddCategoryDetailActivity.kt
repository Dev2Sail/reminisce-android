package studio.hcmc.reminisce.ui.activity.category

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.R
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
        appBar.appbarTitle.text = getText(R.string.add_category_title)
        appBar.appbarBack.setOnClickListener { finish() }
        appBar.appbarActionButton1.setOnClickListener {
            if (viewBinding.addCategoryField.string.length <= 15) {
                Intent(this, HomeActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }

        val inputField = viewBinding.addCategoryField
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.text.length <= 15
        }
    }
}