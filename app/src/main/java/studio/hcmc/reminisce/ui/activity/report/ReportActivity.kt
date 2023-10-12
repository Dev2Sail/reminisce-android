package studio.hcmc.reminisce.ui.activity.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityReportBinding
import studio.hcmc.reminisce.ui.view.Navigation

class ReportActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("selectedMenuId", -1)
        viewBinding.apply {
            reportHeader.commonHeaderTitle.text = getText(R.string.activity_report_title)
            reportHeader.commonHeaderAction1.isVisible = false
            reportNavView.navItems.selectedItemId = menuId
        }

        navController()
    }

    private fun navController() {
        viewBinding.reportNavView.navItems.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_main_home -> {
                    startActivity(Navigation.onNextHome(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_map -> {
                    startActivity(Navigation.onNextMap(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_report -> { true }
                R.id.nav_main_setting -> {
                    startActivity(Navigation.onNextSetting(applicationContext, it.itemId))
                    finish()

                    true
                }

                else -> false
            }
        }
    }
}