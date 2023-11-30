package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountEditNicknameBinding
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text
import studio.hcmc.reminisce.vo.user.UserVO

class AccountSettingEditNicknameActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountEditNicknameBinding
    private lateinit var user: UserVO
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountEditNicknameBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        prepareUser()
    }

    private fun initView() {
        val inputField = viewBinding.settingAccountDetailNicknameField
        val appBar = viewBinding.settingAccountDetailNicknameAppbar
        appBar.appbarTitle.text = getText(R.string.setting_account_detail_nickname_title)
        appBar.appbarActionButton1.text = getText(R.string.appbar_button_text)
        appBar.appbarBack.setOnClickListener { finish() }
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarActionButton1.setOnClickListener {
            if (inputField.string.length <= 20) {
                preparePatch(inputField.string)
            }
        }
        inputField.placeholderText = user.nickname
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.string.length <= 20
        }
    }

    private fun prepareUser() = CoroutineScope(Dispatchers.IO).launch {
        val userInfo = UserExtension.getUser(context)
        val result = runCatching { UserIO.getById(userInfo.id) }
            .onSuccess { user = it }
            .onFailure {
                LocalLogger.e(it)
                CommonError.onDialog(context)
            }
        if (result.isSuccess) {
            withContext(Dispatchers.Main) { initView() }
        }
    }

    private fun preparePatch(input: String) {
        val dto = UserDTO.Patch().apply {
            this.nickname = input
        }
        patchNickname(dto)
    }

    private fun patchNickname(dto: UserDTO.Patch) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        runCatching { UserIO.patch(user.id, dto) }
            .onSuccess {
                val refresh = UserIO.getById(user.id)
                UserExtension.setUser(refresh)
                toAccountSetting(dto.nickname)
            }.onFailure {
                CommonError.onDialog(context)
                LocalLogger.e(it)
            }
    }

    private fun toAccountSetting(nickname: String) {
        Intent()
            .putExtra("isEdited", true)
            .putExtra("nickname", nickname)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }
}