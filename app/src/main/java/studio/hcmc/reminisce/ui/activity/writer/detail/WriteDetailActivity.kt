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
    // locationId getExtra


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
/*
WriterActivity 밑으로 동일 형식으로 Options text 구분자 아래 다녀온 사람, 해시태그, 현재 카테고리


 */