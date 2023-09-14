package studio.hcmc.reminisce.ui.activity.writer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsBinding

class WriteOptionsActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsBinding

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
            Intent(this@WriteOptionsActivity, WriteOptionsAddTagActivity::class.java).apply {
                startActivity(this)
            }
        }

        override fun addFriendClick() {
            Intent(this@WriteOptionsActivity, WriteOptionsSelectFriendActivity::class.java).apply {
                startActivity(this)
            }
        }

        override fun selectCategoryClick() {
            TODO("Not yet implemented")
        }
    }
}