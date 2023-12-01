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
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity

class WriteOptionsActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsBinding

    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val locationId by lazy { intent.getIntExtra("locationId", -1) }
    private val position by lazy { intent.getIntExtra("position", -1) }
    private val visitedAt by lazy { intent.getStringExtra("visitedAt") }
    private val place by lazy { intent.getStringExtra("place") }
    private var title = ""
    private val options = HashMap<String, OptionState>()

    private val tagOptionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onTagResult)
    private val friendOptionsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onFriendResult)
    private val categoryOptionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onCategoryResult)

    private data class OptionState(
        val isEdited: Boolean,
        val body: String?
    )

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
        viewBinding.writeOptionsAppbar.appbarBack.setOnClickListener { divideLaunch() }
        val (year, month, day) = visitedAt!!.split("-")
        viewBinding.writeOptionsSuccessDateMessage.text = getString(R.string.write_options_success_date_message, year, month.removePrefix("0"), day.removePrefix("0"))
        viewBinding.writeOptionsSuccessLocationMessage.text = getString(R.string.write_options_success_location_message, place)
        viewBinding.writeOptionsNextButton.setOnClickListener { WriteOptionsDialog(this, dialogDelegate, title) }
        options["friend"] = OptionState(false, null)
        options["tag"] = OptionState(false, null)
        options["category"] = OptionState(false, null)
    }

    private fun prepareCategory() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { CategoryIO.getById(categoryId) }
            .onSuccess { this@WriteOptionsActivity.title = it.title }
            .onFailure { LocalLogger.e(it) }
    }

    private val dialogDelegate = object : WriteOptionsDialog.Delegate {
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


    private fun toWriteDetail() {
        Intent()
            .putExtra("isAdded", true)
            .putExtra("locationId", locationId)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private fun toModifiedWriteDetail() {
        Intent()
            .putExtra("isModified", true)
            .putExtra("locationId", locationId)
            .putExtra("position", position)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private fun divideLaunch() {
        if (position == -1) {
            toWriteDetail()
        } else {
            toModifiedWriteDetail()
        }
    }

    private fun onTagResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            val body = activityResult.data?.getStringExtra("body")
            options["tag"] = OptionState(true, body)
            dialogChanger(title)
        }
    }

    private fun onFriendResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            val body = activityResult.data?.getStringExtra("body")
            options["friend"] = OptionState(true, body)
            dialogChanger(title)
        }
    }

    private fun onCategoryResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            val body = activityResult.data?.getStringExtra("title")
            options["category"] = OptionState(true, body)
            dialogChanger(title)
        }
    }

    private fun dialogChanger(categoryTitle: String) {
        val dialogViewBinding = DialogWriteOptionsBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this, dialogViewBinding)
        if (options["friend"]!!.isEdited) {
            dialogViewBinding.writeOptionsFriendIcon.setImageResource(R.drawable.round_favorite_24)
            dialogViewBinding.writeOptionsFriendBody.text = options["friend"]!!.body
        }
        if (options["tag"]!!.isEdited) {
            dialogViewBinding.writeOptionsTagIcon.setImageResource(R.drawable.round_favorite_24)
            dialogViewBinding.writeOptionsTagBody.text = options["tag"]!!.body
        }

        dialogViewBinding.writeOptionsNextFriend.setOnClickListener {
            dialogDelegate.onFriendClick()
            dialog.dismiss()
        }
        dialogViewBinding.writeOptionsNextTag.setOnClickListener {
            dialogDelegate.onTagClick()
            dialog.dismiss()
        }
        dialogViewBinding.writeOptionsNextCategory.setOnClickListener {
            dialogDelegate.onCategoryClick()
            dialog.dismiss()
        }
        val categoryName = options["category"]!!.body ?: categoryTitle
        dialogViewBinding.writeOptionsCategoryBody.text = when (categoryName) {
            "Default" -> viewBinding.root.context.getString(R.string.category_view_holder_title)
            "new" -> viewBinding.root.context.getString(R.string.add_category_body)
            else -> categoryName
        }
        dialog.show()
    }
}