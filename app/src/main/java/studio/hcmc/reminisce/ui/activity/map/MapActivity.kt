package studio.hcmc.reminisce.ui.activity.map

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MapConstants
import com.naver.maps.map.util.MarkerIcons
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityMapBinding
import studio.hcmc.reminisce.util.navigationController

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewBinding: ActivityMapBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var mapView: MapView? = null

    private val markerInfo = HashMap<String, Place>()
    private val markers = ArrayList<Marker>(markerInfo.size)
    private val customMarkerInfo = HashMap<String, PlaceWithEmoji>()
    private val customMarkers = ArrayList<Marker>(customMarkerInfo.size)

    private data class Place(
        val address: String,
        val latitude: Double,
        val longitude: Double
    )

    private data class PlaceWithEmoji(
        val emoji: String,
        val address: String,
        val latitude: Double,
        val longitude: Double
    )

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        mapView = viewBinding.navermapMapView
        mapView?.getMapAsync(this)
        mapView?.onCreate(savedInstanceState)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = MapConstants.MIN_ZOOM_KOREA
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5666103, 126.9783882))
//        naverMap.moveCamera(cameraUpdate)

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

        val bounds = LatLngBounds.Builder() // 마커 여러 개
            // user의 location 조회 후 latitude, longitude insert
            .include(LatLng(37.5640984, 126.9712268))
            .build()

        naverMap.isIndoorEnabled = true // 실내지도 활성화

        val mapUiSetting = naverMap.uiSettings
        mapUiSetting.isLocationButtonEnabled = true
        mapUiSetting.isScrollGesturesEnabled = true
        mapUiSetting.isZoomGesturesEnabled = true
        mapUiSetting.isZoomControlEnabled = true
        mapUiSetting.isScaleBarEnabled = true
        mapUiSetting.isCompassEnabled = false

        // TODO permission check
//        ContextCompat.checkSelfPermission(
//            this, android.Manifest.permission.
//        )

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.position = LatLng(37.5670135, 126.9783740)



        val defaultMarker = Marker()
//        defaultMarker.position = LatLng(37.40959, 126.6951245)
        defaultMarker.position = LatLng(37.4095882722825, 126.69511383777056)
        defaultMarker.map = naverMap
        defaultMarker.icon = MarkerIcons.BLACK
        defaultMarker.iconTintColor = getColor(R.color.md_theme_light_primary)
//        defaultMarker.icon = OverlayImage.fromResource(R.drawable.round_favorite_16)
        defaultMarker.width = Marker.SIZE_AUTO
        defaultMarker.height = Marker.SIZE_AUTO
        defaultMarker.isHideCollidedSymbols = true
        // defaultMarker.map = null -> delete marker



        val infoWindow = InfoWindow() // display marker information
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
            override fun getText(p0: InfoWindow): CharSequence {
                return "장소"
            }
        }
//        infoWindow.open(defaultMarker)

        val markerListener = Overlay.OnClickListener {
            val marker = it as Marker

            if (marker.infoWindow == null) {
                infoWindow.open(marker)
            } else {
                infoWindow.close()
            }

            true
        }

        defaultMarker.onClickListener = markerListener


        naverMap.setOnMapClickListener { pointF, latLng ->
            infoWindow.close()
            Toast.makeText(this, "${latLng.latitude}, ${latLng.longitude}", Toast.LENGTH_SHORT).show()
        }
        naverMap.setOnSymbolClickListener {
            if (it.caption == "서울특별시청") {
                Toast.makeText(this, "서울시청 클릭", Toast.LENGTH_SHORT).show()
                // consume event, OnMapClick x
                true
            } else {
                // OnMapClick o

                false
            }
        }
        naverMap.addOnLocationChangeListener { // 사용자 위치 변경 시
            Toast.makeText(this, "${it.latitude}, ${it.longitude} // ${it.accuracy}", Toast.LENGTH_SHORT).show()
        }

        defaultMarker.setOnClickListener {
            Toast.makeText(this, "Marker Click", Toast.LENGTH_SHORT).show()
            // OnMapClick x
            true
        }

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }

            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}