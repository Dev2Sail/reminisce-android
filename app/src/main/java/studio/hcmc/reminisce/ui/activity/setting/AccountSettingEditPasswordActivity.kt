package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.kotlin.crypto.sha512
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountEditPasswordBinding
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AccountSettingEditPasswordActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountEditPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountEditPasswordBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        val appBar = viewBinding.settingAccountDetailPasswordAppbar
        val inputField = viewBinding.settingAccountDetailPasswordField
        appBar.appbarTitle.text = getText(R.string.setting_account_detail_password_title)
        appBar.appbarActionButton1.text = getText(R.string.appbar_button_text)
        appBar.appbarBack.setOnClickListener { finish() }
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarActionButton1.setOnClickListener {
            if (inputField.string.length >= 5) {
//                patchPassword(inputField.string)
                preparePatch(inputField.string)
            }
        }
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.text.length >= 5
        }
    }

    private fun preparePatch(input: String) {
        val sha512Password = input.sha512
        val dto = UserDTO.Patch().apply {
            this.password = sha512Password
        }
        patchPassword(dto, input)
    }

    private fun patchPassword(dto: UserDTO.Patch, plainPassword: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@AccountSettingEditPasswordActivity)
        runCatching { UserIO.patch(user.id, dto) }
            .onSuccess {
//                UserAuthVO(user.email, editedPassword).save(this@AccountSettingEditPasswordActivity)
                patchUserAuthVO(user.email, plainPassword)
                toAccountSetting()
            }.onFailure {
                CommonError.onDialog(this@AccountSettingEditPasswordActivity)
                LocalLogger.e(it)
            }
    }

    private suspend fun patchUserAuthVO(email: String, password: String) {
        UserAuthVO(email, password).save(this)
    }

    private fun toAccountSetting() {
        Intent().putExtra("isEdited", true).setActivity(this, Activity.RESULT_OK)
        finish()
    }
}