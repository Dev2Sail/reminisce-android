package studio.hcmc.reminisce.ui.activity.writer

import android.os.Bundle
import android.util.Log
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

    }

    private fun initView() {
        viewBinding.writeOptionsSelectCategoryAppbar.apply {
            appbarTitle.text = getText(R.string.write_options_select_category_title)
            appbarBack.setOnClickListener { finish() }
            appbarActionButton1.setOnClickListener {

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
                for (category in it) {
                    addCategoryView(category)
                }
            }
            .onFailure {
                Log.v("reminisce Logger", "[reminisce > writeOptions > friend] : msg - ${it.message} ::  localMsg - ${it.localizedMessage} :: cause - ${it.cause}")
            }
    }

    private fun addCategoryView(category: CategoryVO) {
        var checkFlag = false
        /*
        textView는 단일 선택만 가능함
        1) category detail에서 write 한 경우 -> 해당 categoryId 체크
        2) map에서 write 한 경우 -> 기본 categoryId 체크

         */
        val textView = LayoutWriteOptionsSelectCategoryItemBinding.inflate(layoutInflater).apply {
            writeOptionsSelectCategoryTitle.text = category.title
            root.setOnClickListener {


            }

        }
        val divider = MaterialDivider(this)





        viewBinding.writeOptionsSelectCategoryItems.addView(textView.root)
        viewBinding.writeOptionsSelectCategoryItems.addView(divider)

    }

}