package studio.hcmc.reminisce.ui.activity.launcher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.databinding.ActivityLauncherBinding
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.ui.activity.MainActivity
import studio.hcmc.reminisce.ui.activity.home.HomeActivity

class LauncherActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityLauncherBinding

    // TODO LOGIN AUTO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
                prepareSignIn()
            }, 1000
        )
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun prepareSignIn() = CoroutineScope(Dispatchers.IO).launch {
        val context = this@LauncherActivity
        val info = UserAuthVO(context)
        Log.v("info", "user Info : $info")
        if (info != null) {
            launchHome()

            return@launch
        } else {
            launchMain()
        }
    }

    private fun launchHome() {
        Intent(this, HomeActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun launchMain() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
        }
    }
}