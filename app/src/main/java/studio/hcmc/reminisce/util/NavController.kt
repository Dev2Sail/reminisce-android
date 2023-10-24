package studio.hcmc.reminisce.util

import android.app.Activity
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.LayoutBottomNavigationBinding
import studio.hcmc.reminisce.ui.view.Navigation

fun Activity.navigationController(view: LayoutBottomNavigationBinding, currentId: Int) {
    view.navItems.selectedItemId = currentId
    view.navItems.setOnItemSelectedListener {
        when (it.itemId) {
            R.id.nav_main_home -> {
                startActivity(Navigation.onNextHome(this, it.itemId))
                finish()

                true
            }
            R.id.nav_main_map -> {
                startActivity(Navigation.onNextMap(this, it.itemId))
                finish()

                true
            }
            R.id.nav_main_report -> {
                startActivity(Navigation.onNextReport(this, it.itemId))
                finish()

                true
            }
            R.id.nav_main_setting -> {
                startActivity(Navigation.onNextSetting(this, it.itemId))
                finish()

                true
            }
            else -> false
        }
    }

    view.navItems.setOnItemReselectedListener {
        when (it.itemId) {
            R.id.nav_main_home -> {}
            R.id.nav_main_map -> {}
            R.id.nav_main_report -> {}
            R.id.nav_main_setting -> {}
        }
    }
}
