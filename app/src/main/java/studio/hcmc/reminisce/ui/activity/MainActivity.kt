package studio.hcmc.reminisce.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.databinding.ActivityGreetingBinding
import studio.hcmc.reminisce.ui.activity.sign_in.SignInActivity
import studio.hcmc.reminisce.ui.activity.sign_up.SignUpActivity

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityGreetingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGreetingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.greetingSignUpBtn.setOnClickListener { launchSignUp() }
        viewBinding.greetingSignInBtn.setOnClickListener { launchSignIn() }
    }

    private fun launchSignUp() {
        Intent(this, SignUpActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun launchSignIn() {
        Intent(this, SignInActivity::class.java).apply {
            startActivity(this)
        }
    }
}