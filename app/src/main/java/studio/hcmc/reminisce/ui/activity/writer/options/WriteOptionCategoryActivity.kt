package studio.hcmc.reminisce.ui.activity.writer.options

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    private val checkedCategoryId = HashSet<Int>(1)
    private val contents = ArrayList<WriteOptionCategoryAdapter.Content>()
    private val states = HashMap<Int, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsSelectCategoryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.writeOptionsSelectCategoryAppbar.appbarTitle.text = getText(R.string.write_options_select_category_title)
        viewBinding.writeOptionsSelectCategoryAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.writeOptionsSelectCategoryAppbar.appbarActionButton1.setOnClickListener {
//            patchCategory(checkedCategoryId.elementAt(0))
        }
////        checkedCategoryId.add(categoryId)
//        checkedCategoryId.add(31)
////        selectedCategoryId[0] = 31
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
        prepareStates()
    }

    private fun onContentsReady() {
        viewBinding.writeOptionsSelectCategoryItems.layoutManager = LinearLayoutManager(this)
        adapter = WriteOptionCategoryAdapter(adapterDelegate, itemDelegate)
        viewBinding.writeOptionsSelectCategoryItems.adapter = adapter
    }

    private fun patchCategory(categoryId: Int) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.patchCategoryId(locationId, categoryId) }
            .onSuccess { launchOptions() }
            .onFailure { LocalLogger.e(it) }
    }

    private fun launchOptions() {
        Intent().putExtra("isAdded", true).setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private val adapterDelegate = object : WriteOptionCategoryAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private fun prepareStates() {
        for (category in categories) {
            states[category.id] = category.id == categoryId
        }
    }

    private fun buildStates(categoryId: Int) {
        for (item in states.keys) {
            if (item == categoryId) {
                states[categoryId] = true
            } else {
                states[item] = false
            }
        }
    }

    private val itemDelegate = object : WriteOptionCategoryItemViewHolder.Delegate {
        override fun onItemClick(categoryId: Int, position: Int): Boolean {
            buildStates(categoryId)
            return states[categoryId]!!
        }


//        override fun onItemClick(categoryId: Int, position: Int): Boolean {
////            return if (selectedCategoryId[0] != categoryId) {
////                selectedCategoryId[0] = categoryId
////                LocalLogger.v("current categoryId (visible true): ${selectedCategoryId[0]}")
////
////                true
////            } else {
////                LocalLogger.v("current categoryId (visible false): ${selectedCategoryId[0]}")
////                false
////            }
//            // 선택돼있던 상태
//            if (!checkedCategoryId.add(categoryId)) {
//                // 선택 해제
//                checkedCategoryId.remove(categoryId)
//
//                return false
//            }
//            checkedCategoryId.clear()
//            checkedCategoryId.add(categoryId)
//
//            return true
//        }

        override fun validate(categoryId: Int): Boolean {
            return states[categoryId]!!
        }

//        fun validateCheck(position: Int): Boolean {
//            if ()
//
//        }
    }
}
/*
val set = HashSet<Int>(1)
    set.add(1)


    fun test(id: Int): Boolean {
        if (!set.add(id)) {
            set.remove(id)
            set.add(id)
            return false
        }
        set.clear()
        set.add(id)

        return true
    }
 */