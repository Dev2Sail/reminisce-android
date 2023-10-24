package studio.hcmc.reminisce.ui.view

import android.content.Context
import android.content.Intent
import studio.hcmc.reminisce.ui.activity.home.HomeActivity
import studio.hcmc.reminisce.ui.activity.map.MapActivity
import studio.hcmc.reminisce.ui.activity.report.ReportActivity
import studio.hcmc.reminisce.ui.activity.setting.SettingActivity

object Navigation {
    fun onNextHome(context: Context, menuId: Int): Intent {
        return Intent(context, HomeActivity::class.java).apply {
            putExtra("menuId", menuId)
        }
    }
    fun onNextMap(context: Context, menuId: Int): Intent {
        return Intent(context, MapActivity::class.java).apply {
            putExtra("menuId", menuId)
        }
    }

    fun onNextReport(context: Context, menuId: Int): Intent {
        return Intent(context, ReportActivity::class.java).apply {
            putExtra("menuId", menuId)
        }
    }

    fun onNextSetting(context: Context, menuId: Int): Intent {
        return Intent(context, SettingActivity::class.java).apply {
            putExtra("menuId", menuId)
        }
    }
}