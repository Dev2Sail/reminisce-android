package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.content.Context
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
    private var isFriendModified = false
    private var isTagModified = false
    private var isCategoryModified = false
    private val writeOptionsDialog by lazy { WriteOptionsDialog(dialogDelegate) }

    private val activityResult = Intent()
    private val tagOptionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onTagResult)
    private val friendOptionsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onFriendResult)
    private val categoryOptionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onCategoryResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        activityResult.putExtra("locationId", locationId)
        initView()
    }

    private fun initView() {
        prepareCategory()
        initUI()
        viewBinding.writeOptionsAppbar.appbarBack.setOnClickListener {
            activityResult.setActivity(this, Activity.RESULT_OK)
            finish()
        }
        viewBinding.writeOptionsNextButton.setOnClickListener { writeOptionsDialog.show() }
    }

    private fun initUI() {
        viewBinding.writeOptionsAppbar.appbarTitle.text = ""
        viewBinding.writeOptionsAppbar.appbarActionButton1.isVisible = false
        val (year, month, day) = visitedAt!!.split("-")
        viewBinding.writeOptionsSuccessDateMessage.text = getString(R.string.write_options_success_date_message, year, month.removePrefix("0"), day.removePrefix("0"))
        viewBinding.writeOptionsSuccessLocationMessage.text = getString(R.string.write_options_success_location_message, place)
    }

    private fun prepareCategory() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { CategoryIO.getById(categoryId) }
            .onSuccess { dialogDelegate.categoryTitle = it.title }
            .onFailure { LocalLogger.e(it) }
    }

    private val dialogDelegate = object : WriteOptionsDialog.Delegate {
        override val context: Context get() = this@WriteOptionsActivity
        override var friends = ArrayList<String>()
        override var tags = ArrayList<String>()
        override var categoryTitle: String = ""
        override fun isFriendAdded() = isFriendModified
        override fun isTagAdded() = isTagModified
        override fun isCategoryModified() = isCategoryModified
        override fun onTagClick() { launchTagOption() }
        override fun onFriendClick() { launchFriendOption() }
        override fun onCategoryClick() { launchCategoryOption() }
    }

    private fun launchTagOption() {
        val intent = Intent(this, WriteOptionTagActivity::class.java)
            .putExtra("locationId", locationId)
        tagOptionLauncher.launch(intent)
    }

    private fun launchFriendOption() {
        val intent = Intent(this, WriteOptionFriendActivity::class.java)
            .putExtra("locationId", locationId)
        friendOptionsLauncher.launch(intent)
    }

    private fun launchCategoryOption() {
        val intent = Intent(this, WriteOptionCategoryActivity::class.java)
            .putExtra("categoryId", categoryId)
            .putExtra("locationId", locationId)
        categoryOptionLauncher.launch(intent)
    }

    private fun onTagResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            isTagModified = true
            this.activityResult.putExtra("isAdded", true)
            this.activityResult.putExtra("isModified", true)
//            activityResult.data?.getStringExtra("body")?.let { dialogDelegate.friends.add(it) }
        }
        writeOptionsDialog.show()
    }

    private fun onFriendResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            isFriendModified = true
            this.activityResult.putExtra("isAdded", true)
            this.activityResult.putExtra("isModified", true)
//            activityResult.data?.getStringExtra("body")?.let { dialogDelegate.friends.add(it) }
        }
        writeOptionsDialog.show()
    }

    private fun onCategoryResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            isCategoryModified = true
            this.activityResult.putExtra("isAdded", true)
            this.activityResult.putExtra("isModified", true)
            activityResult.data?.getStringExtra("title")?.let { dialogDelegate.categoryTitle = it }
        }
        writeOptionsDialog.show()
    }
}