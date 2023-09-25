package studio.hcmc.reminisce.ui.activity.writer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsBinding
import studio.hcmc.reminisce.ui.activity.writer.options.WriteOptionAddTagActivity
import studio.hcmc.reminisce.ui.activity.writer.options.WriteOptionSelectCategoryActivity
import studio.hcmc.reminisce.ui.activity.writer.options.WriteOptionSelectFriendActivity

class WriteOptionsActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsBinding
    private val currentCategoryId by lazy { intent.getIntExtra("currentCategoryId", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.writeOptionsAppbar.apply {
            appbarTitle.text = ""
            appbarActionButton1.isVisible = false
            appbarBack.setOnClickListener { finish() }
        }

        viewBinding.writeOptionsNextButton.setOnClickListener {
            WriteOptionsDialog(this, writeOptionsDelegate)
        }
    }

    private val writeOptionsDelegate = object : WriteOptionsDialog.Delegate {
        override fun addTagClick() {
            Intent(this@WriteOptionsActivity, WriteOptionAddTagActivity::class.java).apply {
                putExtra("currentCategoryId", currentCategoryId)
                startActivity(this)
            }
        }

        override fun addFriendClick() {
            Intent(this@WriteOptionsActivity, WriteOptionSelectFriendActivity::class.java).apply {
                putExtra("currentCategoryId", currentCategoryId)
                startActivity(this)
            }
        }

        override fun selectCategoryClick() {
            Intent(this@WriteOptionsActivity, WriteOptionSelectCategoryActivity::class.java).apply {
                putExtra("currentCategoryId", currentCategoryId)
                startActivity(this)
            }
        }
    }
}