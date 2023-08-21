package studio.hcmc.reminisce.ui.activity.sign_up

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.kotlin.crypto.sha512
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySignUpBinding
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.launcher.LauncherActivity
import studio.hcmc.reminisce.ui.activity.sign_in.SignInActivity
import studio.hcmc.reminisce.util.string

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.signUpAppbar.appbarTitle.text = getText(R.string.greeting_sign_up)
        viewBinding.signUpAppbar.appbarBack.setOnClickListener { finish() }

        viewBinding.signUpAppbar.appbarActionButton1.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        viewBinding.signUpEmail.editText!!.addTextChangedListener {
            setNextEnabledState()
        }
        viewBinding.signUpPassword.editText!!.addTextChangedListener {
            setNextEnabledState()
        }
        viewBinding.signUpNickname.editText!!.addTextChangedListener {
            setNextEnabledState()
        }
        viewBinding.signUpNext.setOnClickListener {
            val dto = UserDTO.Post().apply {
                email = viewBinding.signUpEmail.string
                password = viewBinding.signUpPassword.string.sha512
                nickname = viewBinding.signUpNickname.string
            }

            CoroutineScope(Dispatchers.IO).launch {
                runCatching { UserIO.signUp(dto) }
                    .onSuccess {
                        Intent(viewBinding.root.context, LauncherActivity::class.java).apply {
                            startActivity(this)
                        }
                    }
                    .onFailure { onFailureDialog() }
            }

            // email 중복 검사

        }
    }

    private fun setNextEnabledState() {
        val inputtedEmail = viewBinding.signUpEmail.editText!!.text.toString()
        val checkedState = Patterns.EMAIL_ADDRESS.matcher(inputtedEmail).matches()
        val inputtedPassword = viewBinding.signUpPassword.editText!!.text.toString()
        val inputtedNickname = viewBinding.signUpNickname.editText!!.text.toString()
        viewBinding.signUpNext.isEnabled = checkedState &&
                (inputtedPassword.isNotEmpty() && inputtedPassword.length >= 5) &&
                (inputtedNickname.isNotEmpty() && inputtedNickname.length <= 20)
    }

    private fun onFailureDialog() = CoroutineScope(Dispatchers.Main).launch {
        MaterialAlertDialogBuilder(this@SignUpActivity)
            .setTitle("Failure")
            .setMessage("회원가입 재실행")
            .setPositiveButton("메롱") { _, _ -> }
            .show()
    }
}
