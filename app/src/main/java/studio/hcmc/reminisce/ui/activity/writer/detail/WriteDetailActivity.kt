package studio.hcmc.reminisce.ui.activity.writer.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.databinding.ActivityWriteDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension

class WriteDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteDetailBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


    }

    private fun initView() {
        viewBinding.writeDetailAppbar.apply {

        }
    }

    private fun fetchContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteDetailActivity)
        // getById로 가져와야 함 -> summary에서 locationId 가지고 있어야 되겠네


    }
}