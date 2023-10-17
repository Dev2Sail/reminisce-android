package studio.hcmc.reminisce.ui.activity.category

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityTagDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension

class TagDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTagDetailBinding
    // private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val tagId by lazy { intent.getIntExtra("tagId", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTagDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


    }

    private fun initView() {
        viewBinding.tagDetailAppbar.appbarTitle.text = getText(R.string.header_view_holder_title)
        viewBinding.tagDetailAppbar.appbarActionButton1.isVisible = false
        viewBinding.tagDetailAppbar.appbarBack.setOnClickListener { finish() }



    }

    private fun prepareContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@TagDetailActivity)
    }
}