package studio.hcmc.reminisce.ui.activity.writer.detail

import android.os.Bundle
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
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
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

    private val contents = ArrayList<WriteDetailAdapter.Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.writeDetailAppbar.apply {
            appbarTitle.text = title
            appbarActionButton1.text = getString(R.string.header_action)
            appbarActionButton1.setOnClickListener {
                // TODO writeActivity intent
            }
            appbarBack.setOnClickListener {finish() }
        }
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
        } else {
            CommonError.onMessageDialog(this@WriteDetailActivity, getString(R.string.dialog_error_common_list_body))
            LocalLogger.e(result.exceptionOrNull()!!)
            LocalLogger.e("Write Detail loadContents Error")
        }
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
}