package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import studio.hcmc.reminisce.databinding.ActivitySettingAccountDetailPasswordBinding
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AccountSettingDetailPasswordActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountDetailPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountDetailPasswordBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val appBar = viewBinding.settingAccountDetailPasswordAppbar
        appBar.appbarTitle.text = "비밀번호 설정"
        appBar.appbarActionButton1.text = "완료"
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarBack.setOnClickListener { finish() }

        val inputField = viewBinding.settingAccountDetailPasswordField
        inputField.placeholderText = "5자 이상 입력해 주세요"
        inputField.endIconMode = END_ICON_CLEAR_TEXT
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = (inputField.text.isNotEmpty() && inputField.text.length >= 5)
        }

        appBar.appbarActionButton1.setOnClickListener {
            val inputtedValue = inputField.string
            Intent(this, AccountSettingActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}