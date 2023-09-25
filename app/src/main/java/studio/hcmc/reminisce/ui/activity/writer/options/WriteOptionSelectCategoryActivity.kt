package studio.hcmc.reminisce.ui.activity.writer.options

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import studio.hcmc.reminisce.vo.category.CategoryVO

class WriteOptionSelectCategoryActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsSelectCategoryBinding
    private lateinit var categories: List<CategoryVO>
    private val currentCategoryId by lazy { intent.getIntExtra("currentCategoryId", -1) }
    private val checkedCategoryId = HashSet<Int>(1)

    // TODO locationId 받아와야 함

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsSelectCategoryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
        prepareCategories()
    }

    private fun initView() {
        viewBinding.writeOptionsSelectCategoryAppbar.apply {
            appbarTitle.text = getText(R.string.write_options_select_category_title)
            appbarBack.setOnClickListener { finish() }
            appbarActionButton1.setOnClickListener {
                Toast.makeText(this@WriteOptionSelectCategoryActivity, "SAVE", Toast.LENGTH_SHORT)
            }
        }

        checkedCategoryId.add(currentCategoryId)
    }

    private fun onContentsReady() {
        viewBinding.writeOptionsSelectCategoryItems.layoutManager = LinearLayoutManager(this)
        viewBinding.writeOptionsSelectCategoryItems.adapter = WriteOptionSelectCategoryAdapter(categoryItemDelegate)
    }

    private fun prepareCategories() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionSelectCategoryActivity)
        runCatching { CategoryIO.listByUserId(user.id) }
            .onSuccess {
                categories = it.sortedBy(CategoryVO::sortOrder)

                withContext(Dispatchers.Main) { onContentsReady() }
            }
            .onFailure {
                Log.v("reminisce Logger", "[reminisce > writeOptions > Prepare category] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
            }
    }

    private fun patchCategory(categoryId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionSelectCategoryActivity)
        // location repository update 만들어야 함 !
        runCatching { LocationIO }
    }

    private val categoryItemDelegate = object : WriteOptionSelectCategoryItemViewHolder.Delegate {
        override val currentCategoryId: Int
            get() = this@WriteOptionSelectCategoryActivity.currentCategoryId

        override fun onItemClick(categoryId: Int): Boolean {
            if (!checkedCategoryId.add(categoryId)) {
                checkedCategoryId.remove(categoryId)
                return false
            }
            checkedCategoryId.clear()
            checkedCategoryId.add(categoryId)
            return true
        }
        override fun getItemCount() = categories.size
        override fun getItem(position: Int) = categories[position]
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