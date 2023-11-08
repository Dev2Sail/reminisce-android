package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsBinding
import studio.hcmc.reminisce.databinding.DialogWriteOptionsBinding
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.ui.activity.writer.options.WriteOptionCategoryActivity
import studio.hcmc.reminisce.ui.activity.writer.options.WriteOptionFriendActivity
import studio.hcmc.reminisce.ui.activity.writer.options.WriteOptionTagActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity

class WriteOptionsActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsBinding
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val locationId by lazy { intent.getIntExtra("locationId", -1) }
    private val visitedAt by lazy { intent.getStringExtra("visitedAt") }
    private val place by lazy { intent.getStringExtra("place") }
    private var title = ""

    private val tagOptionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onTagResult)
    private val friendOptionsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onFriendResult)
    private val categoryOptionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onCategoryResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        prepareCategory()
        viewBinding.writeOptionsAppbar.appbarTitle.text = ""
        viewBinding.writeOptionsAppbar.appbarActionButton1.isVisible = false
        viewBinding.writeOptionsAppbar.appbarBack.setOnClickListener {
            Intent().putExtra("isAdded", true).setActivity(this, Activity.RESULT_OK)
            finish()
        }
        val (year, month, day) = visitedAt!!.split("-")
        viewBinding.writeOptionsSuccessDateMessage.text = getString(R.string.write_options_success_date_message, year, month.trim('0'), day.trim('0'))
        viewBinding.writeOptionsSuccessLocationMessage.text = getString(R.string.write_options_success_location_message, place)
        viewBinding.writeOptionsNextButton.setOnClickListener { WriteOptionsDialog(this, dialogDelegate, title) }
    }

    private fun prepareCategory() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { CategoryIO.getById(categoryId) }
            .onSuccess { title = it.title }
            .onFailure { LocalLogger.e(it) }
    }

    private val dialogDelegate = object : WriteOptionsDialog.Delegate {
        override fun onTagClick() {
            val intent = Intent(this@WriteOptionsActivity, WriteOptionTagActivity::class.java).putExtra("locationId", locationId)
            tagOptionLauncher.launch(intent)
        }

        override fun onFriendClick() {
            val intent = Intent(this@WriteOptionsActivity, WriteOptionFriendActivity::class.java).putExtra("locationId", locationId)
            friendOptionsLauncher.launch(intent)
        }

        override fun onCategoryClick() {
            val intent = Intent(this@WriteOptionsActivity, WriteOptionCategoryActivity::class.java)
                .putExtra("categoryId", categoryId)
                .putExtra("locationId", locationId)
            categoryOptionLauncher.launch(intent)
        }
    }

    private fun onTagResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            val dialogViewBinding = DialogWriteOptionsBinding.inflate(layoutInflater)
            dialogViewBinding.writeOptionsTagIcon.setImageResource(R.drawable.round_favorite_24)
        }
    }

    private fun onFriendResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            val dialogViewBinding = DialogWriteOptionsBinding.inflate(layoutInflater)
            dialogViewBinding.writeOptionsTagIcon.setImageResource(R.drawable.round_favorite_24)
        }
    }

    private fun onCategoryResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            val title = activityResult.data?.getStringExtra("title")
            val dialogViewBinding = DialogWriteOptionsBinding.inflate(layoutInflater)
            dialogViewBinding.writeOptionsCategoryName.text = title
        }
    }
}