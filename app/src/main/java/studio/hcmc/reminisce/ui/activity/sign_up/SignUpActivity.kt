package studio.hcmc.reminisce.ui.activity.sign_up

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.kotlin.crypto.sha512
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySignUpBinding
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.dto.user.UserDTO.Post
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.sign_in.SignInActivity
import studio.hcmc.reminisce.util.LocalLogger
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
        viewBinding.signUpAppbar.appbarTitle.text = getText(R.string.greeting_sign_up)
        viewBinding.signUpAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.signUpAppbar.appbarActionButton1.isVisible = false
        viewBinding.signUpEmail.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signUpPassword.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signUpNickname.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signUpNext.setOnClickListener {
            LocalLogger.v("input user information =======" +
                    "\nemail: ${viewBinding.signUpEmail.string} " +
                    "\nplainPassword: ${viewBinding.signUpPassword.string} " +
                    "\npassword: ${viewBinding.signUpPassword.string.sha512}" +
                    "\nnickname: ${viewBinding.signUpNickname.string}")

//            validateUser(dto)
//            prepareSignUp(viewBinding.signUpEmail.string, viewBinding.signUpPassword.string, viewBinding.signUpNickname.string)
            testGetByEmail(viewBinding.signUpEmail.string)
        }
    }

//    private fun validateUser(dto: UserDTO.Post) = CoroutineScope(Dispatchers.IO).launch {
//        runCatching { UserIO.getByEmail(dto.email) }
//            .onSuccess {
//                onErrorSignUp()
//                LocalLogger.e(it.email)
//            }.onFailure {
//                onSignUp(dto)
//                LocalLogger.v(it)
//            }
//    }
    private fun setNextEnabledState() {
        val inputtedEmail = viewBinding.signUpEmail.string
        val inputtedPassword = viewBinding.signUpPassword.string
        val inputtedNickname = viewBinding.signUpNickname.string
        val checkedState = Patterns.EMAIL_ADDRESS.matcher(inputtedEmail).matches()
        viewBinding.signUpNext.isEnabled = checkedState &&
                (inputtedPassword.isNotEmpty() && inputtedPassword.length >= 5) &&
                (inputtedNickname.isNotEmpty() && inputtedNickname.length <= 20)
    }


    private fun testGetByEmail(email: String) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { UserIO.testEmail(email) }
            .onSuccess {
                LocalLogger.v("success result: $it")
            }.onFailure {
                LocalLogger.e("not found error")
            }
    }

    private fun prepareSignUp(email: String, plainPassword: String, nickname: String) {
        val dto = UserDTO.Post().apply {
            this.email = email
            this.password = plainPassword.sha512
            this.nickname = nickname
        }
        LocalLogger.v("input : ${dto.email}, ${dto.nickname}, ${dto.nickname}")
        try {
            CoroutineScope(Dispatchers.IO).launch { testValidate(dto) }
        } catch (e: Throwable) {
            LocalLogger.e(e)

        }

//        onSignUp(dto)
//        CoroutineScope(Dispatchers.IO).launch { test(dto) }
//        validate(dto) // 성공
    }

    private suspend fun testValidate(dto: Post) = coroutineScope {
        val info = async { UserIO.getByEmail(dto.email) }.await()

    }






    private fun validate(dto: UserDTO.Post) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { UserIO.getByEmail(dto.email) }
            .onSuccess { onErrorSignUp() }
            .onFailure {
                onSignUp(dto)
                LocalLogger.e(it)
            }
    }

    private fun onSignUp(dto: UserDTO.Post) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { UserIO.signUp(dto) }
            .onSuccess { onSuccessSignUp() }
            .onFailure { LocalLogger.e(it) }
    }

    private suspend fun onSuccessSignUp() {
        withContext(Dispatchers.Main) { SignUpMessageDialog(this@SignUpActivity, signUpMessageDelegate) }
    }

    private suspend fun onErrorSignUp() {
        withContext(Dispatchers.Main) { SignUpErrorDialog(this@SignUpActivity, signUpErrorDelegate) }
    }


    private val signUpMessageDelegate = object : SignUpMessageDialog.Delegate {
        override fun onDoneClick() { moveToSignIn() }
    }

    private val signUpErrorDelegate = object : SignUpErrorDialog.Delegate {
        override fun onDoneClick() { textClear() }
    }

    private fun textClear() {
        viewBinding.signUpEmail.text.clear()
        viewBinding.signUpPassword.text.clear()
        viewBinding.signUpNickname.text.clear()
    }

    private fun moveToSignIn() {
        Intent(this, SignInActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}
/*
private suspend fun test(dto: UserDTO.Post) = coroutineScope {
        val validateDeferred = async { UserIO.getByEmail(dto.email) }
        runCatching { validateDeferred.await() }
            .onSuccess { onErrorSignUp() }
            .onFailure {

                onSignUp(dto)
            }
    }
 */