package studio.hcmc.reminisce.ui.view

import android.content.Context
import android.content.Intent
import studio.hcmc.reminisce.ui.activity.home.HomeActivity
import studio.hcmc.reminisce.ui.activity.report.ReportActivity
import studio.hcmc.reminisce.ui.activity.setting.SettingActivity

object Navigation {
    fun onNextHome(context: Context, selectedId: Int): Intent {
        return Intent(context, HomeActivity::class.java).apply {
            putExtra("selectedMenuId", selectedId)
        }
    }
//    fun onNextMap(context: Context, selectedId: Int): Intent {
//        return Intent(context, HomeActivity::class.java).apply {
//            putExtra("selectedMenuId", selectedId)
//        }
//    }

    fun onNextReport(context: Context, selectedId: Int): Intent {
        return Intent(context, ReportActivity::class.java).apply {
            putExtra("selectedMenuId", selectedId)
        }
    }

    fun onNextSetting(context: Context, selectedId: Int): Intent {
        return Intent(context, SettingActivity::class.java).apply {
            putExtra("selectedMenuId", selectedId)
        }
    }
}