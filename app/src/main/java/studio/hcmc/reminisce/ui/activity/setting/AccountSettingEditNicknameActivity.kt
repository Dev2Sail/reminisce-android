package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountEditNicknameBinding
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AccountSettingEditNicknameActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountEditNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountEditNicknameBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        val inputField = viewBinding.settingAccountDetailNicknameField
        val appBar = viewBinding.settingAccountDetailNicknameAppbar

        viewBinding.settingAccountDetailNicknameAppbar.apply {
            appbarTitle.text = getText(R.string.setting_account_detail_nickname_title)
            appbarActionButton1.text = getText(R.string.appbar_button_text)
            appbarBack.setOnClickListener { finish() }
            appbarActionButton1.isEnabled = false
            appbarActionButton1.setOnClickListener {
                if (inputField.string.length <= 20) {
                    patchNickname(inputField.string)
                }
            }
        }

        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.string.length <= 20
        }

        prepareUser()
    }

    private fun prepareUser() = CoroutineScope(Dispatchers.Main).launch {
        val user = UserExtension.getUser(this@AccountSettingEditNicknameActivity)
        val field = viewBinding.settingAccountDetailNicknameField
        runCatching { UserIO.getByEmail(user.email) }
            .onSuccess {
                field.placeholderText = it.nickname
                field.hint = it.nickname
            }
            .onFailure {
                CommonError.onDialog(this@AccountSettingEditNicknameActivity)
                LocalLogger.e(it)
            }
    }

    private fun patchNickname(editedNickname: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@AccountSettingEditNicknameActivity)
        val patchDTO = UserDTO.Patch().apply {
            nickname = editedNickname
        }
        runCatching { UserIO.patch(user.id, patchDTO) }
            .onSuccess {
                Intent(this@AccountSettingEditNicknameActivity, AccountSettingActivity::class.java).apply {
                    finish()
                    startActivity(this)
                }
            }
            .onFailure {
                CommonError.onDialog(this@AccountSettingEditNicknameActivity)
                LocalLogger.e(it)
            }
    }
}