package studio.hcmc.reminisce.ui.activity.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountDetailNicknameBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AccountSettingDetailNicknameActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountDetailNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountDetailNicknameBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val appBar = viewBinding.settingAccountDetailNicknameAppbar
        appBar.appbarTitle.text = getText(R.string.setting_account_detail_nickname_title)
        appBar.appbarActionButton1.text = getText(R.string.appbar_button_text)
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarBack.setOnClickListener { finish() }

        val inputField = viewBinding.settingAccountDetailNicknameField

        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.string.length <= 20
        }

        appBar.appbarActionButton1.setOnClickListener {
            if (inputField.string.length <= 20) {

            }
        }
    }

    private fun prepareUser() = CoroutineScope(Dispatchers.Main).launch {
        val user = UserExtension.getUser(this@AccountSettingDetailNicknameActivity)
        val field = viewBinding.settingAccountDetailNicknameField
        runCatching { UserIO.getByEmail(user.email) }
            .onSuccess {
                field.placeholderText = it.nickname
            }
            .onFailure {  }
    }
}