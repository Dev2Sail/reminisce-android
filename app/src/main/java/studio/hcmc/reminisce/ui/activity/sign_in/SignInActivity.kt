package studio.hcmc.reminisce.ui.activity.sign_in

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.databinding.ActivitySignInBinding
import studio.hcmc.reminisce.ui.activity.home.HomeActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.signInAppbar.appbarTitle.text = "로그인"
        viewBinding.signInAppbar.appbarActionButton1.isVisible = false

        viewBinding.signInAppbar.appbarBack.setOnClickListener {
            finish()
        }
        viewBinding.signInEmail.editText!!.addTextChangedListener {
            setNextEnabledState()
        }
        viewBinding.signInPassword.editText!!.addTextChangedListener {
            setNextEnabledState()
        }
        viewBinding.signInNextBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setNextEnabledState() {
        val inputtedEmail = viewBinding.signInEmail.editText!!.text.toString()
        val checkedState = Patterns.EMAIL_ADDRESS.matcher(inputtedEmail).matches()
        val inputtedPassword = viewBinding.signInPassword.editText!!.text.toString()
        viewBinding.signInNextBtn.isEnabled = checkedState && inputtedPassword.isNotEmpty()
    }

}