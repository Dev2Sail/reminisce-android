package studio.hcmc.reminisce.ui.activity.sign_up

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.kotlin.crypto.sha512
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySignUpBinding
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.home.HomeActivity
import studio.hcmc.reminisce.ui.activity.sign_in.SignInActivity
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.signUpAppbar.apply {
            appbarTitle.text = getText(R.string.greeting_sign_up)
            appbarBack.setOnClickListener { finish() }
            appbarActionButton1.setOnClickListener {
                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                startActivity(intent)
            }
        }

        viewBinding.apply {
            signUpEmail.editText!!.addTextChangedListener { setNextEnabledState() }
            signUpPassword.editText!!.addTextChangedListener { setNextEnabledState() }
            signUpNickname.editText!!.addTextChangedListener { setNextEnabledState() }
        }


        viewBinding.signUpNext.setOnClickListener {
            val signUpDTO = UserDTO.Post().apply {
                email = viewBinding.signUpEmail.string
                password = viewBinding.signUpPassword.string.sha512
                nickname = viewBinding.signUpNickname.string
            }

            CoroutineScope(Dispatchers.IO).launch {
                runCatching { UserIO.getByEmail(signUpDTO.email) }
                    .onSuccess { onSignUpError() }
                    .onFailure {
                        UserIO.signUp(signUpDTO)
                        onSignUpMessage()
                        Log.v("reminisce Logger", "[reminisce > Sign Up > signup] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
                    }
            }
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

    private val signUpMessageDelegate = object : SignUpMessageDialog.Delegate {
        override fun onDoneClick() {
            Intent(this@SignUpActivity, HomeActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private val signUpErrorDelegate = object : SignUpErrorDialog.Delegate {
        override fun onDoneClick() {
            viewBinding.signUpEmail.text.clear()
            viewBinding.signUpPassword.text.clear()
            viewBinding.signUpNickname.text.clear()
        }
    }

    private fun onSignUpMessage() = CoroutineScope(Dispatchers.IO).launch {
        SignUpMessageDialog(this@SignUpActivity, signUpMessageDelegate)
    }

    private fun onSignUpError() = CoroutineScope(Dispatchers.IO).launch {
        SignUpErrorDialog(this@SignUpActivity, signUpErrorDelegate)
    }
}
