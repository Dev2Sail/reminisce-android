package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        initView()
    }

    private fun initView() {
        val appBar = viewBinding.settingAccountDetailPasswordAppbar
        val inputField = viewBinding.settingAccountDetailPasswordField

        viewBinding.settingAccountDetailPasswordAppbar.apply {
            appbarTitle.text = getText(R.string.setting_account_detail_password_title)
            appbarActionButton1.text = getText(R.string.appbar_button_text)
            appbarBack.setOnClickListener { finish() }
            appbarActionButton1.isEnabled = false
            appbarActionButton1.setOnClickListener {
                if (inputField.string.length >= 5) {
                    patchPassword(inputField.string)
                }
            }
        }

        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.text.length >= 5
        }
    }

    private fun patchPassword(editedPassword: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@AccountSettingDetailPasswordActivity)
        val patchDTO = UserDTO.Patch().apply {
            password = editedPassword.sha512
        }
        runCatching { UserIO.patch(user.id, patchDTO) }
            .onSuccess {
                UserAuthVO(user.email, editedPassword).save(this@AccountSettingDetailPasswordActivity)
                CommonMessage.onMessage(this@AccountSettingDetailPasswordActivity, "비밀번호가 변경되었어요.")
                Intent(this@AccountSettingDetailPasswordActivity, AccountSettingActivity::class.java).apply {
                    startActivity(this)
                }
            }
            .onFailure {
                CommonError.onDialog(this@AccountSettingDetailPasswordActivity)
                Log.v("reminisce Logger", "[reminisce > Account Setting PW > patch pw] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
            }
    }
}