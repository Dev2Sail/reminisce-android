package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.kotlin.crypto.sha512
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountDetailPasswordBinding
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.ui.view.CommonMessage
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AccountSettingDetailPasswordActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountDetailPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountDetailPasswordBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val appBar = viewBinding.settingAccountDetailPasswordAppbar
        appBar.appbarTitle.text = getText(R.string.setting_account_detail_password_title)
        appBar.appbarActionButton1.text = getText(R.string.appbar_button_text)
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarBack.setOnClickListener { finish() }
        val inputField = viewBinding.settingAccountDetailPasswordField
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.text.length >= 5
        }
        appBar.appbarActionButton1.setOnClickListener {
            if (inputField.string.length >= 5) {
                patchPassword(inputField.string)
            }
        }
    }

    private fun patchPassword(editedPassword: String) = CoroutineScope(Dispatchers.IO).launch {
        val userAuth = UserExtension.getUser(this@AccountSettingDetailPasswordActivity)
        val user = UserIO.getByEmail(userAuth.email)
        val patchDTO = UserDTO.Patch().apply {
            password = editedPassword.sha512
        }
        runCatching { UserIO.patch(user.id, patchDTO) }
            .onSuccess {
                UserAuthVO(userAuth.email, editedPassword).save(this@AccountSettingDetailPasswordActivity)
                CommonMessage.onMessage(this@AccountSettingDetailPasswordActivity, "비밀번호가 변경되었어요.")
                Intent(this@AccountSettingDetailPasswordActivity, AccountSettingActivity::class.java).apply {
                    startActivity(this)
                }
            }
            .onFailure { CommonError.onDialog(this@AccountSettingDetailPasswordActivity) }
    }
}