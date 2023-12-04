package studio.hcmc.reminisce.ui.activity.writer.options

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsSelectCategoryBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.category.CategoryVO
import studio.hcmc.reminisce.vo.user.UserVO

class WriteOptionCategoryActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsSelectCategoryBinding
    private lateinit var adapter: WriteOptionCategoryAdapter
    private lateinit var user: UserVO

    private val locationId by lazy { intent.getIntExtra("locationId", -1) }
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }

    private val categories = ArrayList<CategoryVO>()
    private val contents = ArrayList<WriteOptionCategoryAdapter.Content>()
    private val mutex = Mutex()
    private var hasMoreContents = true
    private var lastLoadedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsSelectCategoryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.writeOptionsSelectCategoryAppbar.appbarTitle.text = getText(R.string.write_options_select_category_title)
        viewBinding.writeOptionsSelectCategoryAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.writeOptionsSelectCategoryAppbar.appbarActionButton1.isVisible = false
        viewBinding.writeOptionsSelectCategoryItems.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch { loadContents() }
    }

    private suspend fun prepareUser(): UserVO {
        if (!this::user.isInitialized) {
            user = UserExtension.getUser(this)
        }

        return user
    }

    private suspend fun loadContents() = mutex.withLock {
        val user = prepareUser()
        val lastId = categories.lastOrNull()?.id ?: Int.MAX_VALUE
        val delay = System.currentTimeMillis() - lastLoadedAt - 2000
        if (delay < 0) {
            delay(-delay)
        }

        if (!hasMoreContents) {
            return
        }

        try {
            val fetched = CategoryIO.listByUserId(user.id, lastId).sortedBy { it.sortOrder }
            for (category in fetched) {
                categories.add(category)
            }
            hasMoreContents = fetched.size >= 20
            val preSize = contents.size
            val size = prepareContents(fetched)
            withContext(Dispatchers.Main) { onContentsReady(preSize, size) }
            lastLoadedAt = System.currentTimeMillis()
        } catch (e: Throwable) {
            LocalLogger.e(e)
            withContext(Dispatchers.Main) { onError() }
        }
    }

    private fun onError() {
        CommonError.onMessageDialog(this,  getString(R.string.dialog_error_common_list_body))
    }

    private fun prepareContents(fetched: List<CategoryVO>): Int {
        if (contents.lastOrNull() is WriteOptionCategoryAdapter.ProgressContent) {
            contents.removeLast()
        }

        var size = 0
        for (category in fetched) {
            val content = WriteOptionCategoryAdapter.DetailContent(category)
            contents.add(content)
        }
        size += fetched.size

        if (hasMoreContents) {
            contents.add(WriteOptionCategoryAdapter.ProgressContent)
        }

        return size
    }

    private fun onContentsReady(preSize: Int, size: Int) {
        if (!this::adapter.isInitialized) {
            adapter = WriteOptionCategoryAdapter(adapterDelegate, itemDelegate)
            viewBinding.writeOptionsSelectCategoryItems.adapter = adapter
            return
        }

        adapter.notifyItemRangeInserted(preSize, size)
    }

    private val adapterDelegate = object : WriteOptionCategoryAdapter.Delegate {
        override fun hasMoreContents() = hasMoreContents
        override fun getMoreContents() { CoroutineScope(Dispatchers.IO).launch { loadContents() } }
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val itemDelegate = object : WriteOptionCategoryItemViewHolder.Delegate {
        override fun onItemClick(categoryId: Int, title: String) {
            patchCategory(categoryId, title)
        }

        override fun validate(categoryId: Int): Boolean {
            return categoryId == this@WriteOptionCategoryActivity.categoryId
        }
    }

    private fun patchCategory(categoryId: Int, title: String) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.patch(locationId, categoryId) }
            .onSuccess { toOptions(title) }
            .onFailure { LocalLogger.e(it) }
    }

    private fun toOptions(title: String) {
        Intent()
            .putExtra("isModified", true)
            .putExtra("title", title)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }
}