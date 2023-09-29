package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountDetailNicknameBinding
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.ui.view.CommonMessage
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AccountSettingDetailNicknameActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountDetailNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountDetailNicknameBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
        prepareUser()
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
    }

    private fun prepareUser() = CoroutineScope(Dispatchers.Main).launch {
        val user = UserExtension.getUser(this@AccountSettingDetailNicknameActivity)
        val field = viewBinding.settingAccountDetailNicknameField
        runCatching { UserIO.getByEmail(user.email) }
            .onSuccess {
                field.placeholderText = it.nickname
                field.hint = it.nickname
            }
            .onFailure {
                CommonError.onDialog(this@AccountSettingDetailNicknameActivity)
                Log.v("reminisce Logger", "[reminisce > Account Setting Nickname > Prepare user] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
            }
    }

    private fun patchNickname(editedNickname: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@AccountSettingDetailNicknameActivity)
        val patchDTO = UserDTO.Patch().apply {
            nickname = editedNickname
        }
        runCatching { UserIO.patch(user.id, patchDTO) }
            .onSuccess {
                CommonMessage.onMessage(this@AccountSettingDetailNicknameActivity, "닉네임이 변경되었어요.")
                Intent(this@AccountSettingDetailNicknameActivity, AccountSettingActivity::class.java).apply {
                    startActivity(this)
                }
            }
            .onFailure {
                CommonError.onDialog(this@AccountSettingDetailNicknameActivity)
                Log.v("reminisce Logger", "[reminisce > Account Setting > patch user] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
            }
    }
}