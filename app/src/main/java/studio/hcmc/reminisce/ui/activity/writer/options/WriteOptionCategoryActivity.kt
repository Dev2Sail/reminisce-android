package studio.hcmc.reminisce.ui.activity.writer.options

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsSelectCategoryBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.vo.category.CategoryVO

class WriteOptionCategoryActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsSelectCategoryBinding
    private lateinit var categories: List<CategoryVO>
    private lateinit var adapter: WriteOptionCategoryAdapter

    private val locationId by lazy { intent.getIntExtra("locationId", -1) }
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }

    private val contents = ArrayList<WriteOptionCategoryAdapter.Content>()

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
        prepareCategories()
    }

    private fun prepareCategories() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionCategoryActivity)
        val result = runCatching { CategoryIO.listByUserId(user.id) }
            .onSuccess {
                categories = it.sortedBy(CategoryVO::sortOrder)
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        }
    }

    private fun prepareContents() {
        for (category in categories) {
            contents.add(WriteOptionCategoryAdapter.DetailContent(category))
        }
    }

    private fun onContentsReady() {
        viewBinding.writeOptionsSelectCategoryItems.layoutManager = LinearLayoutManager(this)
        adapter = WriteOptionCategoryAdapter(adapterDelegate, itemDelegate)
        viewBinding.writeOptionsSelectCategoryItems.adapter = adapter
    }

    private val adapterDelegate = object : WriteOptionCategoryAdapter.Delegate {
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
            .onSuccess { launchOptions(title) }
            .onFailure { LocalLogger.e(it) }
    }

    private fun launchOptions(title: String) {
        Intent()
            .putExtra("isAdded", true)
            .putExtra("title", title)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }
}