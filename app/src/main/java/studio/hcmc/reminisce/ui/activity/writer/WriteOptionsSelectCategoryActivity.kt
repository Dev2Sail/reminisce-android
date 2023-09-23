package studio.hcmc.reminisce.ui.activity.writer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.divider.MaterialDivider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsSelectCategoryBinding
import studio.hcmc.reminisce.databinding.LayoutWriteOptionsSelectCategoryItemBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.vo.category.CategoryVO

class WriteOptionsSelectCategoryActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsSelectCategoryBinding
    private lateinit var categories: List<CategoryVO>

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
                Toast.makeText(this@WriteOptionsSelectCategoryActivity, "save", Toast.LENGTH_SHORT)
            }
        }


    }

    private fun patchCategory(categoryId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionsSelectCategoryActivity)
        runCatching { LocationIO }
    }

    private fun prepareCategories() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionsSelectCategoryActivity)
        runCatching { CategoryIO.listByUserId(user.id) }
            .onSuccess {
                  categories = it

                for (category in it.sortedBy { it.sortOrder }) {
                    prepareCategoryView(category)
                }
            }
            .onFailure {
                Log.v("reminisce Logger", "[reminisce > writeOptions > friend] : msg - ${it.message} ::  localMsg - ${it.localizedMessage} :: cause - ${it.cause}")
            }
    }

    private fun prepareCategoryView(category: CategoryVO) {
        var checkFlag = false
        val radioView = LayoutWriteOptionsSelectCategoryItemBinding.inflate(layoutInflater).apply {
            writeOptionsSelectCategoryItem.text = category.title
        }
        /*
        textView는 단일 선택만 가능함 -> radio group

        array 내 radio button divider 넣어서 ...

        single select -> radio button or

         */

        val divider = MaterialDivider(this)

        viewBinding.writeOptionsSelectCategoryItems.addView(radioView.root)
        viewBinding.writeOptionsSelectCategoryItems.addView(divider)

        viewBinding.writeOptionsSelectCategoryRadioGroup.clearCheck()
        viewBinding.writeOptionsSelectCategoryRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            Log.v("checkedId", "=== checked Id : $checkedId + ${group.checkedRadioButtonId}")
            Log.v("categoryId", "=== checked category Id : ${category.id} || sort : ${category.sortOrder}" )
        }







    }

    private fun addCategoryView() {
    //        viewBinding.writeOptionsSelectCategoryItems.addView(textView.root)
//        viewBinding.writeOptionsSelectCategoryItems.addView(divider)

    }

}