package studio.hcmc.reminisce.ui.activity.tag.editable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityTagEditableDetailBinding

class TagEditableDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTagEditableDetailBinding
    private val tagId by lazy { intent.getIntExtra("tagId", -1) }
    private val tagBody by lazy { intent.getStringExtra("tagTitle") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTagEditableDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.tagEditableDetailAppbar.appbarTitle.text = tagBody
        viewBinding.tagEditableDetailAppbar.appbarActionButton1.text = getString(R.string.dialog_remove)

    }

}