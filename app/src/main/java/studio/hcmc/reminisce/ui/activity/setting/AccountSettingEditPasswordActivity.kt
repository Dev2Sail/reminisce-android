package studio.hcmc.reminisce.ui.activity.setting

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
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AccountSettingEditPasswordActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountEditPasswordBinding
    private val context = this

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
                preparePatch(inputField.string)
            }
        }
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.text.length >= 5
        }
    }

    private fun preparePatch(input: String) {
        val dto = UserDTO.Patch().apply {
            this.password = input.sha512
        }
        patchPassword(dto, input)
    }

    private fun patchPassword(dto: UserDTO.Patch, plainPassword: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        runCatching { UserIO.patch(user.id, dto) }
            .onSuccess {
                patchUserAuthVO(user.email, plainPassword)
                val user = UserIO.getById(user.id)
                UserExtension.setUser(user)
                moveToAccountSetting()
            }.onFailure {
                CommonError.onDialog(context)
                LocalLogger.e(it)
            }
    }

    private suspend fun patchUserAuthVO(email: String, nextPassword: String) {
        UserAuthVO(email, nextPassword).save(this)
    }

    private fun moveToAccountSetting() {
        finish()
    }
}