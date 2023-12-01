package studio.hcmc.reminisce.ui.activity.tag

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityTagDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.activity.category.ItemDeleteDialog
import studio.hcmc.reminisce.ui.activity.tag.editable.TagEditableDetailActivity
import studio.hcmc.reminisce.ui.activity.writer.detail.WriteDetailActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.tag.TagVO

class TagDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTagDetailBinding
    private lateinit var adapter: TagDetailAdapter
    private lateinit var tag: TagVO

    private val tagEditableLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onModifiedResult)
    private val tagId by lazy { intent.getIntExtra("tagId", -1) }

    private val locations = ArrayList<LocationVO>()
    private val friendInfo = HashMap<Int /* locationId */, List<FriendVO>>()
    private val tagInfo = HashMap<Int /* locationId */, List<TagVO>>()
    private val contents = ArrayList<TagDetailAdapter.Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTagDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.tagDetailAppbar.appbarTitle.text = getString(R.string.header_view_holder_title)
        viewBinding.tagDetailAppbar.appbarActionButton1.isVisible = false
        viewBinding.tagDetailAppbar.appbarBack.setOnClickListener { finish() }
        prepareTag()
    }

    private fun prepareTag() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { TagIO.getById(tagId) }
            .onSuccess {
                tag = it
                loadContents()
            }.onFailure { LocalLogger.e(it)}
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@TagDetailActivity)
        val result = runCatching { LocationIO.listByTagId(tag.id, Int.MAX_VALUE) }
            .onSuccess {
                for (vo in it) {
                    locations.add(vo)
                    tagInfo[vo.id] = TagIO.listByLocationId(vo.id)
                    friendInfo[vo.id] = FriendIO.listByUserIdAndLocationId(user.id, vo.id)
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        } else { onError() }
    }

    private fun onError() {
        CommonError.onMessageDialog(this@TagDetailActivity, getString(R.string.dialog_error_common_list_body))
    }

    private fun prepareContents() {
        contents.add(TagDetailAdapter.HeaderContent(tag.body))
        for ((date, locations) in locations.groupBy { it.createdAt.toString().substring(0, 7) }.entries) {
            val (year, month) = date.split("-")
            contents.add(TagDetailAdapter.DateContent(getString(R.string.card_date_separator, year, month.trim('0'))))
            for (location in locations.sortedByDescending { it.id }) {
                contents.add(TagDetailAdapter.DetailContent(
                    location,
                    tagInfo[location.id].orEmpty(),
                    friendInfo[location.id].orEmpty()
                ))
            }
        }
    }

    private fun onContentsReady() {
        viewBinding.tagDetailItems.layoutManager = LinearLayoutManager(this)
        adapter = TagDetailAdapter(adapterDelegate, headerDelegate, summaryDelegate)
        viewBinding.tagDetailItems.adapter = adapter
    }

    private val adapterDelegate = object : TagDetailAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val headerDelegate = object : TagDetailHeaderViewHolder.Delegate {
        override fun onEditClick() { launchTagEditableDetail(tag.id, tag.body) }
    }

    private val summaryDelegate = object : TagDetailItemViewHolder.Delegate {
        override fun onItemClick(locationId: Int, title: String) {
            moveToWriteDetail(locationId, title)
        }
        override fun onItemLongClick(locationId: Int, position: Int) {
            ItemDeleteDialog(this@TagDetailActivity, deleteDialogDelegate, locationId, position)
        }
    }

    private val deleteDialogDelegate = object : ItemDeleteDialog.Delegate {
        override fun onClick(locationId: Int, position: Int) {
            LocalLogger.v("locationId:$locationId, position:$position")
            var locationIndex = -1
            for (item in locations.withIndex()) {
                if (item.value.id == locationId) {
                    locationIndex = item.index
                }
            }
            deleteContent(locationId, position, locationIndex)
        }
    }

    private fun deleteContent(locationId: Int, position: Int, locationIndex: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.delete(locationId) }
            .onSuccess {
                locations.removeAt(locationIndex)
                tagInfo.remove(locationId)
                friendInfo.remove(locationId)
                withContext(Dispatchers.Main) { adapter.notifyItemRemoved(position) }
                viewBinding.tagDetailAppbar.appbarBack.setOnClickListener { launchModifiedHome() }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun launchModifiedHome() {
        Intent().putExtra("isModified", true).setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private fun launchTagEditableDetail(tagId: Int, body: String) {
        val intent = Intent(this, TagEditableDetailActivity::class.java)
            .putExtra("tagId", tagId)
            .putExtra("tagBody", body)
        tagEditableLauncher.launch(intent)
    }

    private fun onModifiedResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            contents.removeAll { it is TagDetailAdapter.Content }
            loadContents()
        }
    }

    private fun moveToWriteDetail(locationId: Int, title: String) {
        Intent(this, WriteDetailActivity::class.java).apply {
            putExtra("locationId", locationId)
            putExtra("title", title)
            startActivity(this)
        }
    }
}
