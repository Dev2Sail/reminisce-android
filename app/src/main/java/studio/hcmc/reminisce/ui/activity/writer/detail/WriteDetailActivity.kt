package studio.hcmc.reminisce.ui.activity.writer.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.writer.edit.EditWriteActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class WriteDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteDetailBinding
    private lateinit var adapter: WriteDetailAdapter
    private lateinit var location: LocationVO
    private lateinit var categoryInfo: CategoryVO
    private lateinit var tagInfo: List<TagVO>
    private lateinit var friendInfo: List<FriendVO>

    private val locationId by lazy { intent.getIntExtra("locationId", -1) }
    private val title by lazy { intent.getStringExtra("title") }
    private val position by lazy { intent.getIntExtra("position", -1) }

    private val contents = ArrayList<WriteDetailAdapter.Content>()
    private val writeOptionsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onWriteOptionsResult)
    private val editWriteLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onEditWriteResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.writeDetailAppbar.appbarTitle.text = title
        viewBinding.writeDetailAppbar.appbarActionButton1.text = getString(R.string.header_action)
        viewBinding.writeDetailAppbar.appbarActionButton1.setOnClickListener {
            testLaunchEditWrite()
//            launchEditWrite()
        }
        viewBinding.writeDetailAppbar.appbarBack.setOnClickListener { finish() }
        loadContents()
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteDetailActivity)
        val result = runCatching { LocationIO.getById(locationId) }
            .onSuccess {
                location = it
                categoryInfo = CategoryIO.getById(it.categoryId)
                tagInfo = TagIO.listByLocationId(it.id)
                friendInfo = FriendIO.listByUserIdAndLocationId(user.id, it.id)
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        } else { onError() }
    }

    private fun onError() {
        CommonError.onMessageDialog(this, getString(R.string.dialog_error_common_list_body))
    }

    private fun prepareContents() {
        contents.add(WriteDetailAdapter.DetailContent(location))
        contents.add(WriteDetailAdapter.OptionsContent(categoryInfo, tagInfo, friendInfo))
    }

    private fun onContentsReady() {
        viewBinding.writeDetailItems.layoutManager = LinearLayoutManager(this)
        adapter = WriteDetailAdapter(adapterDelegate)
        viewBinding.writeDetailItems.adapter = adapter
    }

    private val adapterDelegate = object : WriteDetailAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private fun patchContent(locationId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteDetailActivity)
        val result = runCatching { LocationIO.getById(locationId) }
            .onSuccess {
                location = it
                categoryInfo = CategoryIO.getById(it.categoryId)
                tagInfo = TagIO.listByLocationId(it.id)
                friendInfo = FriendIO.listByUserIdAndLocationId(user.id, it.id)
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) {
                viewBinding.writeDetailAppbar.appbarTitle.text = location.title
                onContentsReady()
            }
        } else { onError() }
    }

    private fun toAddedCategoryDetail() {
        Intent()
            .putExtra("isAdded", true)
            .putExtra("locationId", location.id)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private fun toModifiedCategoryDetail(position: Int) {
        Intent()
            .putExtra("isModified", true)
            .putExtra("locationId", location.id)
            .putExtra("categoryId", location.categoryId)
            .putExtra("position", position)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private fun launchEditWrite() {
        val intent = Intent(this, EditWriteActivity::class.java)
            .putExtra("locationId", location.id)
            .putExtra("position", position)
        writeOptionsLauncher.launch(intent)
    }

    private fun testLaunchEditWrite() {
        val intent = Intent(this, EditWriteActivity::class.java)
            .putExtra("locationId", location.id)
            .putExtra("position", position)
        editWriteLauncher.launch(intent)
    }

    private fun onEditWriteResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            val locationId = intent.getIntExtra("locationId", -1)
            contents.removeAll { it is WriteDetailAdapter.Content }
            patchContent(locationId!!)
            viewBinding.writeDetailAppbar.appbarBack.setOnClickListener { toAddedCategoryDetail() }
        }
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            val position = activityResult.data?.getIntExtra("position", -1)
            val locationId = activityResult.data?.getIntExtra("locationId", -1)
            contents.removeAll { it is WriteDetailAdapter.Content }
            patchContent(locationId!!)
            viewBinding.writeDetailAppbar.appbarBack.setOnClickListener { toModifiedCategoryDetail(position!!) }
        }
    }

    // 안 넘어옴
    private fun onWriteOptionsResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            val locationId = intent.getIntExtra("locationId", -1)
            contents.removeAll { it is WriteDetailAdapter.Content }
            patchContent(locationId!!)
            viewBinding.writeDetailAppbar.appbarBack.setOnClickListener { toAddedCategoryDetail() }
        }
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            val position = activityResult.data?.getIntExtra("position", -1)
            val locationId = activityResult.data?.getIntExtra("locationId", -1)
            contents.removeAll { it is WriteDetailAdapter.Content }
            patchContent(locationId!!)
            viewBinding.writeDetailAppbar.appbarBack.setOnClickListener { toModifiedCategoryDetail(position!!) }
        }
    }
}