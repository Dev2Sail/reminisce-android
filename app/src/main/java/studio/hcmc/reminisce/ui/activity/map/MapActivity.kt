package studio.hcmc.reminisce.ui.activity.map

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MapConstants
import com.naver.maps.map.util.MarkerIcons
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityMapBinding
import studio.hcmc.reminisce.util.navigationController

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

        initView()

    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.mapNavView, menuId)
        viewBinding.mapHeaderSearchField.setOnClickListener {
            Intent(this@MapActivity, SearchLocationActivity::class.java).apply {
                startActivity(this)
            }
        }

        val marker = Marker()
        marker.position = LatLng(37.40959, 126.6951245)
        marker.map = naverMap
        marker.icon = MarkerIcons.BLACK


        val bounds = LatLngBounds.Builder()
            .include(LatLng(37.5640984, 126.9712268))
            .build()

        naverMap.isIndoorEnabled = true // 실내지도 활성화

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
}