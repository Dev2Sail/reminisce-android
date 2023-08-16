package studio.hcmc.reminisce.ui.activity.launcher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.databinding.ActivityLauncherBinding
import studio.hcmc.reminisce.ui.activity.MainActivity

class LauncherActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            }, 2000
        )
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}