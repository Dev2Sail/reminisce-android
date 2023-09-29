package studio.hcmc.reminisce.ui.activity.map

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.MapConstants
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityMapBinding
import studio.hcmc.reminisce.ui.view.Navigation

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewBinding: ActivityMapBinding
    private lateinit var naverMap: NaverMap
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        mapView = viewBinding.navermapMapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(null)

        val menuId = intent.getIntExtra("selectedMenuId", -1)
        viewBinding.mapNavView.navItems.selectedItemId = menuId
        initView()

    }

    private fun initView() {
        viewBinding.root.setOnClickListener {
            Intent(this, SearchLocationActivity::class.java).apply {
                startActivity(this)
            }
        }
        navController()
//        val mapUISetting = naverMap.uiSettings.apply {
//        }



    }

    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == R.id.nav_main_home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = MapConstants.MIN_ZOOM_KOREA


    }



    private fun navController() {
        viewBinding.mapNavView.navItems.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_main_home -> {
                    startActivity(Navigation.onNextHome(applicationContext, it.itemId))
                    finish()
                    true
                }

                R.id.nav_main_map -> {
                    true
                }

                R.id.nav_main_report -> {
                    startActivity(Navigation.onNextReport(applicationContext, it.itemId))
                    finish()
                    true
                }

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