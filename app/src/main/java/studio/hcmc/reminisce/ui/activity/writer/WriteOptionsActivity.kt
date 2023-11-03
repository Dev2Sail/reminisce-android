package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsBinding
import studio.hcmc.reminisce.ui.activity.writer.options.WriteOptionAddTagActivity
import studio.hcmc.reminisce.ui.activity.writer.options.WriteOptionSelectCategoryActivity
import studio.hcmc.reminisce.ui.activity.writer.options.WriteOptionSelectFriendActivity
import studio.hcmc.reminisce.util.setActivity

class WriteOptionsActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsBinding
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val locationId by lazy { intent.getIntExtra("locationId", -1) }
    private val visitedAt by lazy { intent.getStringExtra("visitedAt") }
    private val place by lazy { intent.getStringExtra("place") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.writeOptionsAppbar.appbarTitle.text = ""
        viewBinding.writeOptionsAppbar.appbarActionButton1.isVisible = false
        viewBinding.writeOptionsAppbar.appbarBack.setOnClickListener {
            Intent().putExtra("isAdded", true).setActivity(this, Activity.RESULT_OK)
            finish()
        }
        val (year, month, day) = visitedAt!!.split("-")
        viewBinding.writeOptionsSuccessDateMessage.text = getString(R.string.write_options_success_date_message, year, month.trim('0'), day.trim('0'))
        viewBinding.writeOptionsSuccessLocationMessage.text = getString(R.string.write_options_success_location_message, place)
        viewBinding.writeOptionsNextButton.setOnClickListener { WriteOptionsDialog(this, writeOptionsDelegate) }
    }

    private val writeOptionsDelegate = object : WriteOptionsDialog.Delegate {
        override fun onTagClick() {
            Intent(this@WriteOptionsActivity, WriteOptionAddTagActivity::class.java).apply {
                putExtra("locationId", locationId)
                startActivity(this)
            }
        }

        override fun onFriendClick() {
            Intent(this@WriteOptionsActivity, WriteOptionSelectFriendActivity::class.java).apply {
                putExtra("locationId", locationId)
                startActivity(this)
            }
        }

        override fun onCategoryClick() {
            Intent(this@WriteOptionsActivity, WriteOptionSelectCategoryActivity::class.java).apply {
                putExtra("categoryId", categoryId)
                putExtra("locationId", locationId)
                startActivity(this)
            }
        }
    }
}